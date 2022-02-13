package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.aiConfig;
import static com.triton.util.ObjectHelper.hasPos;
import static proto.triton.FilteredObject.Robot;

public class KickFromPosition extends Skill {
    private final Robot actor;
    private final Vector2d kickFrom;
    private final Vector2d target;
    private final float kickSpeed;
    private final PathfindGridGroup pathfindGridGroup;

    public KickFromPosition(Module module,
                            Robot actor,
                            Vector2d kickFrom,
                            Vector2d target,
                            float kickSpeed,
                            PathfindGridGroup pathfindGridGroup) {
        super(module);
        this.actor = actor;
        this.kickFrom = kickFrom;
        this.target = target;
        this.kickSpeed = kickSpeed;
        this.pathfindGridGroup = pathfindGridGroup;
    }

    @Override
    protected void execute() {
        if (hasPos(actor, kickFrom, aiConfig.kickFromPosDistTolerance)) {
            KickToTarget kickToTarget = new KickToTarget(module, actor, target, kickSpeed);
            submitSkill(kickToTarget);
        } else {
            PathToTarget pathToTarget = new PathToTarget(module,
                    actor,
                    kickFrom,
                    target,
                    pathfindGridGroup);
            submitSkill(pathToTarget);
        }
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }
}
