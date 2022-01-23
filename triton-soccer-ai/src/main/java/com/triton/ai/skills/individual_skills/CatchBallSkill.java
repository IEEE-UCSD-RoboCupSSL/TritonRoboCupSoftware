package com.triton.ai.skills.individual_skills;

import com.triton.helper.Vector2d;
import com.triton.module.Module;

import java.io.IOException;
import java.util.HashMap;

import static com.triton.messaging.Exchange.AI_INDIVIDUAL_SKILL;
import static proto.triton.AiIndividualSkills.*;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class CatchBallSkill {
    public static void catchBallSkill(Module module, int id, CatchBall catchBall, Ball ball, HashMap<Integer, Robot> allies) throws IOException {
        if (allies == null || allies.get(id) == null) return;
        Robot ally = allies.get(id);

        Vector2d allyPos = new Vector2d(ally.getX(), ally.getY());
        Vector2d ballPos = new Vector2d(ball.getX(), ball.getY());
        Vector2d ballVel = new Vector2d(ball.getVx(), ball.getVy());

        Vector2d diff = allyPos.sub(ballPos);
        Vector2d offset = diff.proj(ballVel);
        Vector2d targetPos = ballPos.add(offset);

        IndividualSkill.Builder pathToPointSkill = IndividualSkill.newBuilder();
        PathToPoint.Builder pathToPoint = PathToPoint.newBuilder();
        pathToPoint.setX(targetPos.x);
        pathToPoint.setY(targetPos.y);
        pathToPoint.setOrientation(ballVel.scale(-1).angle());
        pathToPointSkill.setPathToPoint(pathToPoint);

        module.publish(AI_INDIVIDUAL_SKILL, pathToPointSkill.build());
    }
}
