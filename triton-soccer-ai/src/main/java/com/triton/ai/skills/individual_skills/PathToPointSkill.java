package com.triton.ai.skills.individual_skills;

import com.triton.helper.Vector2d;
import com.triton.module.Module;
import com.triton.module.ai_module.helper.PathfindingModule;

import java.io.IOException;
import java.util.HashMap;

import static com.triton.messaging.Exchange.AI_BASIC_SKILL;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiBasicSkills.MoveToPoint;
import static proto.triton.AiIndividualSkills.PathToPoint;
import static proto.triton.ObjectWithMetadata.Robot;

public class PathToPointSkill {
    public static void pathFindToPointSkill(Module module, int id, PathToPoint pathToPoint, HashMap<Integer, Robot> allies) throws IOException {
        if (allies == null) return;

        Robot ally = allies.get(id);
        if (ally == null) return;

        Vector2d from = new Vector2d(ally.getX(), ally.getY());
        Vector2d to = new Vector2d(pathToPoint.getX(), pathToPoint.getY());
        Vector2d nextPoint = PathfindingModule.findPath(from, to, ally);
        if (nextPoint == null) return;

        System.out.println(from);

        BasicSkill.Builder moveToPointSkill = BasicSkill.newBuilder();
        moveToPointSkill.setId(id);
        MoveToPoint.Builder moveToPoint = MoveToPoint.newBuilder();
        moveToPoint.setX(nextPoint.x);
        moveToPoint.setY(nextPoint.y);
        if (pathToPoint.getFacePoint()) {
            float targetOrientation = (float) Math.atan2(pathToPoint.getFaceY() - ally.getY(), pathToPoint.getFaceX() - ally.getX());
            moveToPoint.setOrientation(targetOrientation);
        } else {
            moveToPoint.setOrientation(pathToPoint.getOrientation());
        }
        moveToPointSkill.setMoveToPoint(moveToPoint);
        module.publish(AI_BASIC_SKILL, moveToPointSkill.build());
    }
}
