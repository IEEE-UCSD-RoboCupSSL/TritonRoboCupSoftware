package com.triton.ai.skills.individual_skills;

import com.triton.module.Module;

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

        BasicSkill.Builder moveToPointSkill = BasicSkill.newBuilder();
        moveToPointSkill.setId(id);
        MoveToPoint.Builder moveToPoint = MoveToPoint.newBuilder();
        // TODO Add Pathfinding


        moveToPoint.setX(pathToPoint.getX());
        moveToPoint.setY(pathToPoint.getY());

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
