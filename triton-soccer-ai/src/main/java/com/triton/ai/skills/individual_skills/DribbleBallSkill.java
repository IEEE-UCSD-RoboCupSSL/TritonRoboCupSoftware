package com.triton.ai.skills.individual_skills;

import com.triton.helper.Vector2d;
import com.triton.messaging.Exchange;
import com.triton.module.Module;

import java.io.IOException;

import static proto.triton.AiIndividualSkills.*;
import static proto.triton.AiIndividualSkills.DribbleBall;

public class DribbleBallSkill {
    public static void dribbleBallSkill(Module module, int id, DribbleBall dribbleBall, float ballRadius, float robotRadius) throws IOException {
        IndividualSkill.Builder pathToPointSkill = IndividualSkill.newBuilder();
        PathToPoint.Builder pathToPoint = PathToPoint.newBuilder();

        Vector2d ballTargetPos = new Vector2d(dribbleBall.getX(), dribbleBall.getY());
        Vector2d offset = new Vector2d((float) Math.cos(dribbleBall.getOrientation()), (float) Math.sin(dribbleBall.getOrientation()));
        offset = offset.scale(ballRadius + robotRadius);

        Vector2d allyTargetPos = ballTargetPos.sub(offset);

        pathToPoint.setX(allyTargetPos.x);
        pathToPoint.setY(allyTargetPos.y);
        pathToPoint.setOrientation(dribbleBall.getOrientation());

        pathToPointSkill.setPathToPoint(pathToPoint);

        module.publish(Exchange.AI_BASIC_SKILL, pathToPointSkill.build());
    }
}
