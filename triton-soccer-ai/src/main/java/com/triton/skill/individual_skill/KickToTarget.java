package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.Kick;
import com.triton.util.ObjectHelper;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.aiConfig;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.FilteredObject.Robot;

public class KickToTarget extends Skill {
    private final Robot actor;
    private final Vector2d target;
    private final float kickSpeed;

    public KickToTarget(Module module,
                        Robot actor,
                        Vector2d target,
                        float kickSpeed) {
        super(module);
        this.actor = actor;
        this.target = target;
        this.kickSpeed = kickSpeed;
    }

    @Override
    protected void execute() {
        if (ObjectHelper.hasOrientation(actor, target, aiConfig.kickToPointAngleTolerance)) {
            Kick kick = new Kick(module, actor, kickSpeed, false);
            submitSkill(kick);
        } else {
            Vector2d allyPos = getPos(actor);
            MoveToTarget moveToTarget = new MoveToTarget(module, actor, allyPos, target);
            submitSkill(moveToTarget);
        }
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }
}
