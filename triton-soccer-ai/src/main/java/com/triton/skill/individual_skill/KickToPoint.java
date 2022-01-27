package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.Kick;
import com.triton.util.ObjectHelper;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.RuntimeConstants.aiConfig;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.ObjectWithMetadata.Robot;

public class KickToPoint extends Skill {
    private final Robot ally;
    private final Vector2d pos;

    public KickToPoint(Module module,
                       Robot ally,
                       Vector2d pos) {
        super(module);
        this.ally = ally;
        this.pos = pos;
    }

    @Override
    protected void execute() {
        if (ObjectHelper.matchFacePoint(ally, pos, aiConfig.kickToPointAngleTolerance)) {
            Kick kick = new Kick(module, ally, true, false);
            submitSkill(kick);
        } else {
            Vector2d allyPos = getPos(ally);
            MoveToPoint moveToPoint = new MoveToPoint(module, ally, allyPos, pos);
            submitSkill(moveToPoint);
        }
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }
}
