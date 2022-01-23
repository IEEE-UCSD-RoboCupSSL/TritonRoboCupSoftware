package com.triton.ai.skills.basic_skills;

import com.triton.constant.RuntimeConstants;
import com.triton.helper.PIDControl;
import com.triton.module.Module;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.triton.messaging.Exchange.AI_BASIC_SKILL;
import static proto.triton.AiBasicSkills.*;
import static proto.triton.ObjectWithMetadata.Robot;

public class MoveToPointSkill {
    private static Map<Integer, PIDControl> pidControlsPosX;
    private static Map<Integer, PIDControl> pidControlsPosY;
    private static Map<Integer, PIDControl> pidControlsOrientation;

    public static void moveToPointSkill(Module module, int id, MoveToPoint moveToPoint, Robot ally) throws IOException {
        init(id);
        if (ally == null) return;

        long timestamp = System.currentTimeMillis();

        PIDControl pidControlPosX = pidControlsPosX.get(id);
        PIDControl pidControlPosY = pidControlsPosY.get(id);
        PIDControl pidControlOrientation = pidControlsOrientation.get(id);

        float velX = pidControlPosX.compute(moveToPoint.getX(), ally.getX(), timestamp);
        float velY = pidControlPosY.compute(moveToPoint.getY(), ally.getY(), timestamp);
        float angular = pidControlOrientation.compute(moveToPoint.getOrientation(), ally.getOrientation(), timestamp);

        BasicSkill.Builder matchVelocitySkill = BasicSkill.newBuilder();
        matchVelocitySkill.setId(id);
        MatchVelocity.Builder matchVelocity = MatchVelocity.newBuilder();
        matchVelocity.setVx(velX);
        matchVelocity.setVy(velY);
        matchVelocity.setAngular(angular);
        matchVelocitySkill.setMatchVelocity(matchVelocity);
        module.publish(AI_BASIC_SKILL, matchVelocitySkill.build());
    }

    private static void init(int id) {
        if (pidControlsPosX == null)
            pidControlsPosX = new HashMap<>();
        if (pidControlsPosY == null)
            pidControlsPosY = new HashMap<>();
        if (pidControlsOrientation == null)
            pidControlsOrientation = new HashMap<>();

        pidControlsPosX.putIfAbsent(id, new PIDControl(RuntimeConstants.aiConfig.kpPos,
                RuntimeConstants.aiConfig.kiPos,
                RuntimeConstants.aiConfig.kdPos));
        pidControlsPosY.putIfAbsent(id, new PIDControl(RuntimeConstants.aiConfig.kpPos,
                RuntimeConstants.aiConfig.kiPos,
                RuntimeConstants.aiConfig.kdPos));
        pidControlsOrientation.putIfAbsent(id, new PIDControl(RuntimeConstants.aiConfig.kpOrientation,
                RuntimeConstants.aiConfig.kiOrientation,
                RuntimeConstants.aiConfig.kdOrientation));
    }
}
