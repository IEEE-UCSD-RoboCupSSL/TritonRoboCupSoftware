package com.triton.search.node2d;

import com.triton.constant.RuntimeConstants;
import com.triton.util.Vector2d;
import com.triton.search.base.Graph;
import com.triton.search.base.RouteFinder;
import com.triton.search.base.Scorer;
import org.apache.commons.collections4.iterators.ReverseListIterator;

import java.util.*;

import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathfindField {
    SSL_GeometryFieldSize field;
    HashMap<Integer, Robot> allies, foes;
    Robot excludeAlly;
    Map<Vector2d, Node2d> nodeMap;
    Map<Node2d, Set<Node2d>> connections;

    public PathfindField(SSL_GeometryFieldSize field) {
        this.field = field;
        generateNodeMap();
        generateConnections();
    }

    public static Node2d getNearestNode(Map<Vector2d, Node2d> nodeMap, Vector2d pos) {
        float nodeSpacing = RuntimeConstants.aiConfig.nodeRadius * 2;
        float nearestX = Math.round(pos.x / nodeSpacing) * nodeSpacing;
        float nearestY = Math.round(pos.y / nodeSpacing) * nodeSpacing;
        Vector2d nearestPos = new Vector2d(nearestX, nearestY);
        return nodeMap.get(nearestPos);
    }

    public void generateNodeMap() {
        Map<Vector2d, Node2d> nodeMap = new HashMap<>();

        float pathfindFieldWidth = field.getFieldWidth() / 2f + RuntimeConstants.aiConfig.pathfindExtend;
        float pathfindFieldLength = field.getFieldLength() / 2f + RuntimeConstants.aiConfig.pathfindExtend;

        float nodeSpacing = RuntimeConstants.aiConfig.nodeRadius * 2;

        for (float x = 0; x < pathfindFieldWidth; x += nodeSpacing) {
            for (float y = 0; y < pathfindFieldLength; y += nodeSpacing) {
                Vector2d pos = new Vector2d(x, y);
                Node2d node = new Node2d(pos);
                nodeMap.put(pos, node);

                Vector2d pos1 = new Vector2d(-x, y);
                Node2d node1 = new Node2d(pos1);
                nodeMap.put(pos1, node1);

                Vector2d pos2 = new Vector2d(x, -y);
                Node2d node2 = new Node2d(pos2);
                nodeMap.put(pos2, node2);

                Vector2d pos3 = new Vector2d(-x, -y);
                Node2d node3 = new Node2d(pos3);
                nodeMap.put(pos3, node3);
            }
        }
        this.nodeMap = nodeMap;
    }

    public void generateConnections() {
        float nodeSpacing = RuntimeConstants.aiConfig.nodeRadius * 2;
        Map<Node2d, Set<Node2d>> connections = new HashMap<>();
        nodeMap.forEach((pos, node) -> {
            Set<Node2d> neighbors = new HashSet<>();
            connections.put(node, neighbors);

            for (float offsetX = -nodeSpacing; offsetX <= nodeSpacing; offsetX += nodeSpacing) {
                for (float offsetY = -nodeSpacing; offsetY <= nodeSpacing; offsetY += nodeSpacing) {
                    if (offsetX + offsetY == 0 || offsetX == 0 && offsetY == 0)
                        continue;

                    Vector2d offset = new Vector2d(offsetX, offsetY);
                    Vector2d neighborPos = pos.add(offset);

                    if (nodeMap.containsKey(neighborPos)) {
                        Node2d neighbor = nodeMap.get(neighborPos);
                        neighbors.add(neighbor);
                    }
                }
            }
        });
        this.connections = connections;
    }

    public void updateObstacles(HashMap<Integer, Robot> allies, HashMap<Integer, Robot> foes, Robot excludeAlly) {
        this.allies = allies;
        this.foes = foes;
        this.excludeAlly = excludeAlly;

        float nodeSpacing = RuntimeConstants.aiConfig.nodeRadius * 2;
        nodeMap.forEach((pos, node) -> {
            if (checkCollision(pos))
                node.setObstacle(100000);

            for (float offsetX = -nodeSpacing; offsetX <= nodeSpacing; offsetX += nodeSpacing) {
                for (float offsetY = -nodeSpacing; offsetY <= nodeSpacing; offsetY += nodeSpacing) {
                    if (offsetX == 0 && offsetY == 0)
                        continue;

                    Vector2d offset = new Vector2d(offsetX, offsetY);
                    Vector2d neighborPos = pos.add(offset);

                    if (nodeMap.containsKey(neighborPos)) {
                        Node2d neighbor = nodeMap.get(neighborPos);
                        if (checkCollision(neighborPos))
                            neighbor.setObstacle(100000);
                    }
                }
            }
        });
    }

    public List<Node2d> findRoute(Vector2d from, Vector2d to) {
        Graph<Node2d> graph = new Graph<>(new HashSet<>(nodeMap.values()), connections);
        Scorer<Node2d> nextNodeScorer = new Euclidean2dScorer();
        Scorer<Node2d> targetScorer = new Euclidean2dScorer();
        RouteFinder<Node2d> routeFinder = new RouteFinder<>(graph, nextNodeScorer, targetScorer);

        Node2d fromNode = getNearestNode(nodeMap, from);
        fromNode.setObstacle(100000);
        Node2d toNode = getNearestNode(nodeMap, to);

        if (fromNode == null) {
            System.out.println("From node not on field.");
            return null;
        } else if (toNode == null) {
            System.out.println("To node not on field.");
            return null;
        }

        List<Node2d> route;
        try {
            route = routeFinder.findRoute(fromNode, toNode);
        } catch (IllegalStateException e) {
            route = new ArrayList<>();
            route.add(fromNode);
            return route;
        }

        route.add(new Node2d(to));
        return route;
    }

    public Vector2d findNext(Vector2d from, Vector2d to) {
        return findNext(findRoute(from, to));
    }

    public boolean checkCollision(Vector2d pos) {
        if (field == null || allies == null || foes == null) return true;

        float nodeCollisionDist;
        nodeCollisionDist = 2 * RuntimeConstants.aiConfig.nodeRadius + RuntimeConstants.aiConfig.nodeSafety;
        float boundCollisionDist = RuntimeConstants.objectConfig.robotRadius / 1000f + nodeCollisionDist;
        float robotCollisionDist = 2 * RuntimeConstants.objectConfig.robotRadius / 1000f + nodeCollisionDist;

        if (Math.abs(pos.x) > (field.getFieldWidth() / 2f - boundCollisionDist)) {
            return true;
        } else if (Math.abs(pos.y) > (field.getFieldLength() / 2f - boundCollisionDist)) {
            return true;
        }

        for (Robot ally : allies.values()) {
            if (ally.equals(excludeAlly)) continue;
            Vector2d allyPos = new Vector2d(ally.getX(), ally.getY());
            if (pos.dist(allyPos) < robotCollisionDist)
                return true;
        }

        for (Robot foe : foes.values()) {
            Vector2d foePos = new Vector2d(foe.getX(), foe.getY());
            if (pos.dist(foePos) < robotCollisionDist)
                return true;
        }

        return false;
    }

    public boolean checkCollision(Vector2d from, Vector2d to) {
        Vector2d step = to.sub(from).norm().scale(RuntimeConstants.aiConfig.nodeRadius);
        Vector2d current = from;
        while (current.dist(to) > RuntimeConstants.aiConfig.nodeRadius) {
            if (checkCollision(current))
                return true;
            current = current.add(step);
        }
        return false;
    }

    public Vector2d findNext(List<Node2d> route) {
        Node2d fromNode = route.get(0);
        ReverseListIterator<Node2d> reverseListIterator = new ReverseListIterator<>(route);
        while (reverseListIterator.hasNext()) {
            Node2d toNode = reverseListIterator.next();
            if (!checkCollision(fromNode.getPos(), toNode.getPos()) && toNode != fromNode)
                return toNode.getPos();
        }
        return route.get(1).getPos();
    }

    public Map<Vector2d, Node2d> getNodeMap() {
        return nodeMap;
    }

    public Map<Node2d, Set<Node2d>> getConnections() {
        return connections;
    }
}
