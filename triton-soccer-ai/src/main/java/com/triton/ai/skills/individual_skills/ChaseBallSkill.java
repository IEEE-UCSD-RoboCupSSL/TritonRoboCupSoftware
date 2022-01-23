package com.triton.ai.skills.individual_skills;

import com.triton.module.Module;

import java.io.IOException;

import static com.triton.messaging.Exchange.AI_BASIC_SKILL;
import static com.triton.messaging.Exchange.AI_INDIVIDUAL_SKILL;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiBasicSkills.Dribble;
import static proto.triton.AiIndividualSkills.*;
import static proto.triton.ObjectWithMetadata.Ball;

public class ChaseBallSkill {
    public static void chaseBallSkill(Module module, int id, ChaseBall chaseBall, Ball ball) throws IOException {
        IndividualSkill.Builder pathToPointSkill = IndividualSkill.newBuilder();
        pathToPointSkill.setId(id);
        PathToPoint.Builder pathToPoint = PathToPoint.newBuilder();
        pathToPoint.setX(ball.getX());
        pathToPoint.setY(ball.getY());
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
