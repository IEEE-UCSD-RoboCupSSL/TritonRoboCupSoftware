package com.triton.skill.basic_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.util.Vector2d;

import static com.triton.constant.RuntimeConstants.aiConfig;
import static proto.triton.ObjectWithMetadata.Robot;

public class MoveToPointSkill extends Skill {
    //    private final PIDControl pidControlX;
//    private final PIDControl pidControlY;
//    private final PIDControl pidControlOrientation;
    private final Robot ally;
    private final Vector2d pos;
    private final float orientation;

    public MoveToPointSkill(Module module, Robot ally, Vector2d pos, float orientation) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = orientation;

//        pidControlX = new PIDControl(RuntimeConstants.aiConfig.kpPos,
//                RuntimeConstants.aiConfig.kiPos,
//                RuntimeConstants.aiConfig.kdPos);
//        pidControlY = new PIDControl(RuntimeConstants.aiConfig.kpPos,
//                RuntimeConstants.aiConfig.kiPos,
//                RuntimeConstants.aiConfig.kdPos);
//        pidControlOrientation = new PIDControl(RuntimeConstants.aiConfig.kpOrientation,
//                RuntimeConstants.aiConfig.kiOrientation,
//                RuntimeConstants.aiConfig.kdOrientation);
    }

    @Override
    public void run() {
//        long timestamp = System.currentTimeMillis();
//        float velX = pidControlX.compute(pos.x, ally.getX(), timestamp);
//        float velY = pidControlY.compute(pos.y, ally.getY(), timestamp);
//        float angular = pidControlOrientation.compute(orientation, ally.getOrientation(), timestamp);

        Vector2d vel = pos.sub(new Vector2d(ally.getX(), ally.getY())).scale(aiConfig.kpPos);
        float angular = aiConfig.kpOrientation * (orientation - ally.getOrientation());

        MatchVelocitySkill matchVelocitySkill = new MatchVelocitySkill(module, ally, vel, angular);
        matchVelocitySkill.start();
    }
}
