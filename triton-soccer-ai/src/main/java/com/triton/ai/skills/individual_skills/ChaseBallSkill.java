package com.triton.ai.skills.individual_skills;

import com.triton.helper.Vector2d;
import com.triton.module.Module;

import java.io.IOException;
import java.util.HashMap;

import static com.triton.messaging.Exchange.AI_BASIC_SKILL;
import static com.triton.messaging.Exchange.AI_INDIVIDUAL_SKILL;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiBasicSkills.Dribble;
import static proto.triton.AiIndividualSkills.*;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class ChaseBallSkill {
    public static void chaseBallSkill(Module module, int id, ChaseBall chaseBall, Ball ball, HashMap<Integer, Robot> allies) throws IOException {
        Robot ally = allies.get(id);
        if (ally == null) return;

        Vector2d allyPos = new Vector2d(ally.getX(), ally.getY());
        Vector2d ballPos = new Vector2d(ball.getX(), ball.getY());
        Vector2d offset = ballPos.sub(allyPos).norm().scale(100f);
        Vector2d targetPos = ballPos.add(offset);

        IndividualSkill.Builder pathToPointSkill = IndividualSkill.newBuilder();
        pathToPointSkill.setId(id);
        PathToPoint.Builder pathToPoint = PathToPoint.newBuilder();
        pathToPoint.setX(targetPos.x);
        pathToPoint.setY(targetPos.y);
        pathToPoint.setFaceX(targetPos.x);
        pathToPoint.setFaceY(targetPos.y);
        pathToPoint.setFacePoint(true);
        pathToPointSkill.setPathToPoint(pathToPoint);
        module.publish(AI_INDIVIDUAL_SKILL, pathToPointSkill.build());

        BasicSkill.Builder dribbleSkill = BasicSkill.newBuilder();
        dribbleSkill.setId(id);
        Dribble.Builder dribble = Dribble.newBuilder();
        dribble.setDribbleOn(true);
        dribbleSkill.setDribble(dribble);
        module.publish(AI_BASIC_SKILL, dribbleSkill.build());
    }
}
