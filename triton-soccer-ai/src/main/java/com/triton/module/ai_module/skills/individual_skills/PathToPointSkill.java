package com.triton.module.ai_module.skills.individual_skills;

import com.rabbitmq.client.Delivery;
import com.triton.helper.Vector2d;
import com.triton.module.SkillModule;
import com.triton.module.ai_module.skills.basic_skills.MoveToPointSkill;
import com.triton.search.node2d.Node2d;
import com.triton.search.node2d.PathfindField;
import proto.triton.AiDebugInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathToPointSkill extends SkillModule {
    private int allyId;
    private Vector2d pos;
    private float orientation;
    private Vector2d facePos;

    private SSL_GeometryFieldSize field;
    private HashMap<Integer, Robot> allies;
    private HashMap<Integer, Robot> foes;

    private MoveToPointSkill moveToPointSkill;
    private PathfindField pathfindField;

    public PathToPointSkill(int allyId, Vector2d pos, float orientation) {
        super();
        this.allyId = allyId;
        this.pos = pos;
        this.orientation = orientation;
        this.facePos = null;
    }

    public PathToPointSkill(int allyId, Vector2d pos, Vector2d facePos) {
        this.allyId = allyId;
        this.pos = pos;
        this.facePos = facePos;
    }

    @Override
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
        declareConsume(AI_BIASED_FIELD, this::callbackField);
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
        declareConsume(AI_FILTERED_FOES, this::callbackFoes);
        declarePublish(AI_BIASED_ROBOT_COMMAND);
        declarePublish(AI_DEBUG);
    }

    private void callbackField(String s, Delivery delivery) {
        field = (SSL_GeometryFieldSize) simpleDeserialize(delivery.getBody());
    }

    private void callbackAllies(String s, Delivery delivery) {
        allies = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    private void callbackFoes(String s, Delivery delivery) {
        foes = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    @Override
    public void run() {
        if (field == null || allies == null || foes == null) return;

        if (pathfindField == null)
            pathfindField = new PathfindField(field);

        Robot ally = allies.get(allyId);
        pathfindField.updateObstacles(allies, foes, ally);

        Vector2d from = new Vector2d(ally.getX(), ally.getY());
        List<Node2d> route = pathfindField.findRoute(from, pos);
        Vector2d next = pathfindField.findNext(route);

        if (facePos != null)
            orientation = (float) Math.atan2(facePos.y - ally.getY(), facePos.x - ally.getX());

        if (moveToPointSkill == null) {
            moveToPointSkill = new MoveToPointSkill(allyId, next, orientation);
            scheduleSkill(moveToPointSkill);
        } else {
            moveToPointSkill.setAllyId(allyId);
            moveToPointSkill.setPos(next);
            moveToPointSkill.setOrientation(orientation);
        }

        publishDebug(route, from, pos, next);
    }

    private void publishDebug(List<Node2d> route, Vector2d from, Vector2d to, Vector2d next) {
        AiDebugInfo.Debug.Builder debug = AiDebugInfo.Debug.newBuilder();
        AiDebugInfo.DebugPath.Builder path = AiDebugInfo.DebugPath.newBuilder();
        path.setId(allyId);
        path.setFromPos(AiDebugInfo.DebugVector.newBuilder().setX(from.x).setY(from.y));
        path.setToPos(AiDebugInfo.DebugVector.newBuilder().setX(to.x).setY(to.y));
        path.setNextPos(AiDebugInfo.DebugVector.newBuilder().setX(next.x).setY(next.y));
        route.forEach(node -> {
            Vector2d pos = node.getPos();
            path.addNodes(AiDebugInfo.DebugVector.newBuilder().setX(pos.x).setY(pos.y));
        });
        debug.setPath(path);
        publish(AI_DEBUG, debug.build());
    }

    public void setAllyId(int allyId) {
        this.allyId = allyId;
    }

    public void setPos(Vector2d pos) {
        this.pos = pos;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
        facePos = null;
    }

    public void setFacePos(Vector2d facePos) {
        this.facePos = facePos;
    }
}
