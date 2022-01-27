package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.MatchVelocity;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.aiConfig;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.ObjectWithMetadata.Robot;

public class MoveToPoint extends Skill {
    private final Robot ally;
    private final Vector2d pos;
    private final float orientation;
    private final Vector2d facePos;

    public MoveToPoint(Module module, Robot ally, Vector2d pos, float orientation) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = orientation;
        this.facePos = null;
    }

    public MoveToPoint(Module module, Robot ally, Vector2d pos, Vector2d facePos) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = 0;
        this.facePos = facePos;
    }

    @Override
    protected void execute() {
        Vector2d vel = pos.sub(getPos(ally)).scale(aiConfig.kpPos);
        float targetOrientation;
        if (facePos != null)
            targetOrientation = (float) Math.atan2(facePos.y - ally.getY(), facePos.x - ally.getX());
        else
            targetOrientation = orientation;
        float angular = aiConfig.kpOrientation * (Vector2d.angleDifference(ally.getOrientation(), targetOrientation));

        MatchVelocity matchVelocity = new MatchVelocity(module, ally, vel, angular);
        submitSkill(matchVelocity);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
