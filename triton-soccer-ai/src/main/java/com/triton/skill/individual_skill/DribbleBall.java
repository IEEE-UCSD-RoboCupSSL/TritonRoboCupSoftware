package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.objectConfig;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class DribbleBall extends Skill {
    private final Robot ally;
    private final Vector2d pos;
    private final PathfindGridGroup pathfindGridGroup;
    private final Ball ball;
    private final Map<Integer, Robot> allies;
    private final Map<Integer, Robot> foes;
    private final float orientation;
    private final Vector2d facePos;

    public DribbleBall(Module module,
                       Robot ally,
                       Vector2d pos,
                       float orientation,
                       PathfindGridGroup pathfindGridGroup,
                       Ball ball,
                       Map<Integer, Robot> allies,
                       Map<Integer, Robot> foes) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = orientation;
        this.facePos = null;
        this.pathfindGridGroup = pathfindGridGroup;
        this.ball = ball;
        this.allies = allies;
        this.foes = foes;
    }

    public DribbleBall(Module module,
                       Robot ally,
                       Vector2d pos,
                       Vector2d facePos,
                       PathfindGridGroup pathfindGridGroup,
                       Ball ball,
                       Map<Integer, Robot> allies,
                       Map<Integer, Robot> foes) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = 0;
        this.facePos = facePos;
        this.pathfindGridGroup = pathfindGridGroup;
        this.ball = ball;
        this.allies = allies;
        this.foes = foes;
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

        PathToPoint pathToPoint;
        if (facePos == null)
            pathToPoint = new PathToPoint(module,
                    ally,
                    allyTargetPos,
                    orientation,
                    pathfindGridGroup);
        else
            pathToPoint = new PathToPoint(module,
                    ally,
                    allyTargetPos,
                    facePos,
                    pathfindGridGroup);
        submitSkill(pathToPoint);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
