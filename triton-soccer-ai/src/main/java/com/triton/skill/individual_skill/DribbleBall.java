package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.objectConfig;
import static proto.triton.FilteredObject.Robot;

public class DribbleBall extends Skill {
    private final Robot actor;
    private final Vector2d pos;
    private final PathfindGridGroup pathfindGridGroup;
    private final float orientation;
    private final Vector2d facePos;

    public DribbleBall(Module module,
                       Robot actor,
                       Vector2d pos,
                       float orientation,
                       PathfindGridGroup pathfindGridGroup) {
        super(module);
        this.actor = actor;
        this.pos = pos;
        this.orientation = orientation;
        this.facePos = null;
        this.pathfindGridGroup = pathfindGridGroup;
    }

    public DribbleBall(Module module,
                       Robot actor,
                       Vector2d pos,
                       Vector2d facePos,
                       PathfindGridGroup pathfindGridGroup) {
        super(module);
        this.actor = actor;
        this.pos = pos;
        this.orientation = 0;
        this.facePos = facePos;
        this.pathfindGridGroup = pathfindGridGroup;
    }

    @Override
    protected void execute() {
        Vector2d offset;
        if (facePos == null)
            offset = new Vector2d((float) Math.cos(orientation), (float) Math.sin(orientation));
        else
            offset = facePos.sub(pos).norm();
        offset = offset.scale(objectConfig.objectToCameraFactor * objectConfig.ballRadius
                + objectConfig.objectToCameraFactor * objectConfig.robotRadius);
        Vector2d allyTargetPos = pos.sub(offset);

        PathToTarget pathToTarget;
        if (facePos == null)
            pathToTarget = new PathToTarget(module,
                    actor,
                    allyTargetPos,
                    orientation,
                    pathfindGridGroup);
        else
            pathToTarget = new PathToTarget(module,
                    actor,
                    allyTargetPos,
                    facePos,
                    pathfindGridGroup);
        submitSkill(pathToTarget);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
