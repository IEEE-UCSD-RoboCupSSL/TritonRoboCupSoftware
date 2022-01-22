package com.triton.ai.skills.individual_skills;

import com.triton.messaging.Exchange;
import com.triton.module.Module;
import proto.triton.AiBasicSkills;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.HashMap;

import static com.triton.messaging.Exchange.*;
import static proto.triton.AiBasicSkills.*;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiIndividualSkills.PathToPoint;
import static proto.vision.MessagesRobocupSslDetection.*;

public class PathToPointSkill {
    public static void pathFindToPointSkill(Module module, int id, PathToPoint pathToPoint, HashMap<Integer, SSL_DetectionRobot> allies) throws IOException {
        if (allies == null) return;

        SSL_DetectionRobot ally = allies.get(id);
        if (ally == null) return;

        BasicSkill.Builder moveToPointSkill = BasicSkill.newBuilder();
        moveToPointSkill.setId(id);

        MoveToPoint.Builder moveToPoint = MoveToPoint.newBuilder();
        // TODO Add Pathfinding
        moveToPoint.setX(pathToPoint.getX());
        moveToPoint.setY(pathToPoint.getY());
        switch (pathToPoint.getTargetOrientationCase()) {
            case ORIENTATION -> moveToPoint.setOrientation(pathToPoint.getOrientation());
            case FACE_POINT -> {
                if (pathToPoint.getFacePoint()) {
                    float targetOrientation = (float) Math.atan2(pathToPoint.getY() - ally.getY(), pathToPoint.getX() - ally.getX());
                    moveToPoint.setOrientation(targetOrientation);
                }
            }
        }
        moveToPointSkill.setMoveToPoint(moveToPoint);

        module.publish(AI_BASIC_SKILL, moveToPointSkill.build());
    }
}
