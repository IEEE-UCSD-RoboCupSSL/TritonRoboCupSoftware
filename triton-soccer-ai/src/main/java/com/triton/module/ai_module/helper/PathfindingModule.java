package com.triton.module.ai_module.helper;

import com.rabbitmq.client.Delivery;
import com.triton.config.AIConfig;
import com.triton.config.ConfigPath;
import com.triton.config.ObjectConfig;
import com.triton.helper.Vector2d;
import com.triton.module.Module;
import com.triton.search.base.Graph;
import com.triton.search.base.RouteFinder;
import com.triton.search.base.Scorer;
import com.triton.search.node2d.Euclidean2dScorer;
import com.triton.search.node2d.Node2d;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static com.triton.config.ConfigPath.*;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathfindingModule extends Module {
    private static ObjectConfig objectConfig;
    private static AIConfig aiConfig;

    private static SSL_GeometryFieldSize field;
    private static HashMap<Integer, Robot> allies;
    private static HashMap<Integer, Robot> foes;

    public PathfindingModule() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        objectConfig = (ObjectConfig) readConfig(OBJECT_CONFIG);
        aiConfig = (AIConfig) readConfig(AI_CONFIG);
    }

    public static Vector2d findPath(Vector2d from, Vector2d to, Robot ally) {
        if (field == null || allies == null || foes == null) return null;

        Map<Vector2d, Node2d> nodeMap = new HashMap<>();
        Map<Node2d, Set<Node2d>> connections = new HashMap<>();

        float fieldWidth = field.getFieldWidth();
        float fieldLength = field.getFieldLength();

        Node2d fromNode = null;
        float fromMinDist = Float.MAX_VALUE;
        Node2d toNode = null;
        float toMinDist = Float.MAX_VALUE;

        for (float x = (-fieldWidth / 2); x < fieldWidth / 2; x += aiConfig.nodeSize) {
            for (float y = (-fieldLength / 2); y < fieldLength; y += aiConfig.nodeSize) {
                Vector2d pos = new Vector2d(x, y);
                Node2d node = new Node2d(pos);
                nodeMap.put(pos, node);

                float fromDist = pos.dist(from);
                if (fromDist < fromMinDist) {
                    fromNode = node;
                    fromMinDist = fromDist;
                }

                float toDist = pos.dist(to);
                if (toDist < toMinDist) {
                    toNode = node;
                    toMinDist = toDist;
                }
            }
        }

        nodeMap.forEach((pos, node) -> {
            Set<Node2d> neighbors = new HashSet<>();
            for (float offsetX = -aiConfig.nodeSize; offsetX <= aiConfig.nodeSize; offsetX += aiConfig.nodeSize) {
                for (float offsetY = -aiConfig.nodeSize; offsetY <= aiConfig.nodeSize; offsetY += aiConfig.nodeSize) {
                    if (offsetX == 0 && offsetY == 0)
                        continue;

                    Vector2d offset = new Vector2d(offsetX, offsetY);
                    Vector2d neighborPos = pos.add(offset);

                    if (!nodeMap.containsKey(neighborPos)) continue;
                    Node2d neighbor = nodeMap.get(neighborPos);
                    if (checkCollision(neighborPos, fieldWidth, fieldLength, ally)) continue;
                    neighbors.add(neighbor);
                }
            }
            connections.put(node, neighbors);
        });

        Graph<Node2d> graph = new Graph<>(new HashSet<>(nodeMap.values()), connections);
        Scorer<Node2d> nextNodeScorer = new Euclidean2dScorer();
        Scorer<Node2d> targetScorer = new Euclidean2dScorer();
        RouteFinder<Node2d> routeFinder = new RouteFinder<>(graph, nextNodeScorer, targetScorer);

        List<Node2d> route = routeFinder.findRoute(fromNode, toNode);
        if (route.size() > 1)
            return route.get(1).getPos();
        return route.get(0).getPos();
    }

    private static boolean checkCollision(Vector2d pos, float fieldWidth, float fieldLength, Robot excludeAlly) {
        float robotCollisionDist = 2 * objectConfig.robotRadius / 1000f;
        float boundCollisionDist = objectConfig.robotRadius / 1000f;

        for (Robot ally : allies.values()) {
            if (ally.equals(excludeAlly)) continue;
            Vector2d allyPos = new Vector2d(ally.getX(), ally.getY());
            if (pos.dist(allyPos) < robotCollisionDist)
                return true;
        }

        for (Robot foe : foes.values()) {
            if (foe.equals(excludeAlly)) continue;
            Vector2d foePos = new Vector2d(foe.getX(), foe.getY());
            if (pos.dist(foePos) < robotCollisionDist)
                return true;
        }

        if (Math.abs(pos.x) > ((fieldWidth / 2) - boundCollisionDist)) {
            return true;
        } else if (Math.abs(pos.y) > ((fieldLength / 2) - boundCollisionDist)) {
            return true;
        }

        return false;
    }

    @Override
    protected void prepare() {
        super.prepare();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_FIELD, this::callbackField);
        declareConsume(AI_FILTERED_BIASED_ALLIES, this::callbackAllies);
        declareConsume(AI_FILTERED_BIASED_FOES, this::callbackFoes);
    }

    private void callbackField(String s, Delivery delivery) {
        this.field = (SSL_GeometryFieldSize) simpleDeserialize(delivery.getBody());
    }

    private void callbackAllies(String s, Delivery delivery) {
        this.allies = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    private void callbackFoes(String s, Delivery delivery) {
        this.foes = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }
}
