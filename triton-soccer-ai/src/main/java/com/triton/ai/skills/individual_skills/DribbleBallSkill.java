package com.triton.ai.skills.individual_skills;

import com.triton.helper.Vector2d;
import com.triton.module.Module;

import java.io.IOException;

import static com.triton.messaging.Exchange.AI_INDIVIDUAL_SKILL;
import static proto.triton.AiIndividualSkills.*;

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
        pathToPoint.setFaceX(dribbleBall.getFaceX());
        pathToPoint.setFaceY(dribbleBall.getFaceY());
        pathToPoint.setFacePoint(dribbleBall.getFacePoint());
        pathToPointSkill.setPathToPoint(pathToPoint);
        module.publish(AI_INDIVIDUAL_SKILL, pathToPointSkill.build());
    }
}
