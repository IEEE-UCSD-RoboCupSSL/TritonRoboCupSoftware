package com.triton.skill.individual_skill;

import com.triton.util.Vector2d;
import com.triton.module.Module;
import com.triton.search.node2d.Node2d;
import com.triton.search.node2d.PathfindField;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.MoveToPointSkill;
import proto.triton.AiDebugInfo;

import java.util.HashMap;
import java.util.List;

import static com.triton.messaging.Exchange.AI_DEBUG;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathToPointSkill extends Skill {
    private Robot ally;
    private Vector2d pos;
    private float orientation;
    private Vector2d facePos;
    private SSL_GeometryFieldSize field;
    private HashMap<Integer, Robot> allies;
    private HashMap<Integer, Robot> foes;

    private PathfindField pathfindField;

    public PathToPointSkill(Module module,
                            Robot ally,
                            Vector2d pos,
                            float orientation,
                            SSL_GeometryFieldSize field,
                            HashMap<Integer, Robot> allies,
                            HashMap<Integer, Robot> foes) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = orientation;
        this.field = field;
        this.allies = allies;
        this.foes = foes;
    }

    public PathToPointSkill(Module module,
                            Robot ally,
                            Vector2d pos,
                            Vector2d facePos,
                            SSL_GeometryFieldSize field,
                            HashMap<Integer, Robot> allies,
                            HashMap<Integer, Robot> foes) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.facePos = facePos;
        this.field = field;
        this.allies = allies;
        this.foes = foes;
    }

    @Override
    public void run() {
        if (pathfindField == null)
            pathfindField = new PathfindField(field);

        pathfindField.updateObstacles(allies, foes, ally);
        Vector2d from = new Vector2d(ally.getX(), ally.getY());
        List<Node2d> route = pathfindField.findRoute(from, pos);
        Vector2d next = pathfindField.findNext(route);

        if (facePos != null)
            orientation = (float) Math.atan2(facePos.y - ally.getY(), facePos.x - ally.getX());

        MoveToPointSkill moveToPointSkill = new MoveToPointSkill(module, ally, next, orientation);
        moveToPointSkill.start();

        publishDebug(route, from, pos, next);
    }

    private void publishDebug(List<Node2d> route, Vector2d from, Vector2d to, Vector2d next) {
        AiDebugInfo.Debug.Builder debug = AiDebugInfo.Debug.newBuilder();
        AiDebugInfo.DebugPath.Builder path = AiDebugInfo.DebugPath.newBuilder();
        path.setId(ally.getId());
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
}
