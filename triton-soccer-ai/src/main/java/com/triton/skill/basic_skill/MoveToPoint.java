package com.triton.skill.basic_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.util.Vector2d;

import static com.triton.constant.RuntimeConstants.aiConfig;
import static proto.triton.ObjectWithMetadata.Robot;

public class MoveToPoint extends Skill {
    private final Robot ally;
    private final Vector2d pos;
    private final float orientation;

    public MoveToPoint(Module module, Robot ally, Vector2d pos, float orientation) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = orientation;
    }

    @Override
    protected void execute() {
        Vector2d vel = pos.sub(new Vector2d(ally.getX(), ally.getY())).scale(aiConfig.kpPos);
        float angular = aiConfig.kpOrientation * (orientation - ally.getOrientation());

        MatchVelocity matchVelocity = new MatchVelocity(module, ally, vel, angular);
        submitSkill(matchVelocity);
    }
}
