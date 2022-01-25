package com.triton.skill.basic_skill;

import com.triton.constant.RuntimeConstants;
import com.triton.helper.PIDControl;
import com.triton.helper.Vector2d;
import com.triton.module.Module;
import com.triton.skill.Skill;

import static proto.triton.ObjectWithMetadata.Robot;

public class MoveToPointSkill extends Skill {
    private final PIDControl pidControlX;
    private final PIDControl pidControlY;
    private final PIDControl pidControlOrientation;
    private Robot ally;
    private Vector2d pos;
    private float orientation;
    private MatchVelocitySkill matchVelocitySkill;

    public MoveToPointSkill(Module module, Robot ally, Vector2d pos, float orientation) {
        super(module);
        update(ally, pos, orientation);

        pidControlX = new PIDControl(RuntimeConstants.aiConfig.kpPos,
                RuntimeConstants.aiConfig.kiPos,
                RuntimeConstants.aiConfig.kdPos);
        pidControlY = new PIDControl(RuntimeConstants.aiConfig.kpPos,
                RuntimeConstants.aiConfig.kiPos,
                RuntimeConstants.aiConfig.kdPos);
        pidControlOrientation = new PIDControl(RuntimeConstants.aiConfig.kpOrientation,
                RuntimeConstants.aiConfig.kiOrientation,
                RuntimeConstants.aiConfig.kdOrientation);
    }

    public void update(Robot ally, Vector2d pos, float orientation) {
        this.ally = ally;
        this.pos = pos;
        this.orientation = orientation;
    }

    @Override
    public void run() {
        long timestamp = System.currentTimeMillis();
        float velX = pidControlX.compute(pos.x, ally.getX(), timestamp);
        float velY = pidControlY.compute(pos.y, ally.getY(), timestamp);
        float angular = pidControlOrientation.compute(orientation, ally.getOrientation(), timestamp);
        Vector2d vel = new Vector2d(velX, velY);

        if (matchVelocitySkill == null) {
            matchVelocitySkill = new MatchVelocitySkill(module, ally, vel, angular);
            scheduleSkill(matchVelocitySkill);
        } else {
            matchVelocitySkill.update(ally, vel, angular);
        }
    }
}
