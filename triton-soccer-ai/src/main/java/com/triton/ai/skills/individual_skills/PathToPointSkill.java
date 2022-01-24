package com.triton.ai.skills.individual_skills;

import com.triton.helper.Vector2d;
import com.triton.module.Module;
import com.triton.search.node2d.Node2d;
import com.triton.search.node2d.PathfindField;
import proto.triton.AiDebugInfo;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.triton.messaging.Exchange.AI_BASIC_SKILL;
import static com.triton.messaging.Exchange.AI_DEBUG;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiBasicSkills.MoveToPoint;
import static proto.triton.AiDebugInfo.*;
import static proto.triton.AiIndividualSkills.PathToPoint;
import static proto.triton.ObjectWithMetadata.Robot;

public class PathToPointSkill {
    public static void pathFindToPointSkill(Module module, int id, PathToPoint pathToPoint, MessagesRobocupSslGeometry.SSL_GeometryFieldSize field, HashMap<Integer, Robot> allies, HashMap<Integer, Robot> foes) throws IOException {
        if (allies == null) return;

        Robot ally = allies.get(id);
        if (ally == null) return;

        Vector2d from = new Vector2d(ally.getX(), ally.getY());
        Vector2d to = new Vector2d(pathToPoint.getX(), pathToPoint.getY());

        PathfindField pathfindField = new PathfindField(field);
        pathfindField.updateConnections(allies, foes, ally);
        List<Node2d> route = pathfindField.findRoute(from, to);
        Vector2d nextPos = pathfindField.findNextPosInRoute(route);

        BasicSkill.Builder moveToPointSkill = BasicSkill.newBuilder();
        moveToPointSkill.setId(id);
        MoveToPoint.Builder moveToPoint = MoveToPoint.newBuilder();
        moveToPoint.setX(nextPos.x);
        moveToPoint.setY(nextPos.y);
        if (pathToPoint.getFacePoint()) {
            float targetOrientation = (float) Math.atan2(pathToPoint.getFaceY() - ally.getY(), pathToPoint.getFaceX() - ally.getX());
            moveToPoint.setOrientation(targetOrientation);
        } else {
            moveToPoint.setOrientation(pathToPoint.getOrientation());
        }
        moveToPointSkill.setMoveToPoint(moveToPoint);
        module.publish(AI_BASIC_SKILL, moveToPointSkill.build());

        Debug.Builder debug = Debug.newBuilder();
        DebugPath.Builder path = DebugPath.newBuilder();
        path.setId(id);
        path.setFromPos(DebugVector.newBuilder().setX(from.x).setY(from.y));
        path.setToPos(DebugVector.newBuilder().setX(to.x).setY(to.y));
        path.setNextPos(DebugVector.newBuilder().setX(nextPos.x).setY(nextPos.y));
        route.forEach(node -> {
            Vector2d pos = node.getPos();
            path.addNodes(DebugVector.newBuilder().setX(pos.x).setY(pos.y));
        });
        debug.setPath(path);
        module.publish(AI_DEBUG, debug.build());
    }
}
