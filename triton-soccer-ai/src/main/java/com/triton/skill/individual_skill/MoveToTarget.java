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

public class MoveToTarget extends Skill {
    private final Robot actor;
    private final Vector2d target;
    private final float orientation;
    private final Vector2d facePos;

    public MoveToTarget(Module module, Robot actor, Vector2d target, float orientation) {
        super(module);
        this.actor = actor;
        this.target = target;
        this.orientation = orientation;
        this.facePos = null;
    }

    public MoveToTarget(Module module, Robot actor, Vector2d target, Vector2d facePos) {
        super(module);
        this.actor = actor;
        this.target = target;
        this.orientation = 0;
        this.facePos = facePos;
    }

    @Override
    protected void execute() {
        Vector2d vel = target.sub(getPos(actor)).scale(aiConfig.kpPos);
        float targetOrientation;
        if (facePos != null)
            targetOrientation = (float) Math.atan2(facePos.y - actor.getY(), facePos.x - actor.getX());
        else
            targetOrientation = orientation;
        float angular = aiConfig.kpOrientation * (Vector2d.angleDifference(actor.getOrientation(), targetOrientation));

        MatchVelocity matchVelocity = new MatchVelocity(module, actor, vel, angular);
        submitSkill(matchVelocity);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
