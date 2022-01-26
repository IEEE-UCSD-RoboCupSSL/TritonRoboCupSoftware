package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.node2d.PathfindGrid;
import com.triton.skill.Skill;
import com.triton.util.Vector2d;

import java.util.HashMap;

import static com.triton.constant.RuntimeConstants.objectConfig;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class DribbleBallSkill extends Skill {
    private final Robot ally;
    private final Vector2d pos;
    private final PathfindGrid pathfindGrid;
    private final Ball ball;
    private final HashMap<Integer, Robot> allies;
    private final HashMap<Integer, Robot> foes;
    private final float orientation;
    private final Vector2d facePos;

    public DribbleBallSkill(Module module,
                            Robot ally,
                            Vector2d pos,
                            float orientation,
                            PathfindGrid pathfindGrid,
                            Ball ball,
                            HashMap<Integer, Robot> allies,
                            HashMap<Integer, Robot> foes) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = orientation;
        this.facePos = null;
        this.pathfindGrid = pathfindGrid;
        this.ball = ball;
        this.allies = allies;
        this.foes = foes;
    }

    public DribbleBallSkill(Module module,
                            Robot ally,
                            Vector2d pos,
                            Vector2d facePos,
                            PathfindGrid pathfindGrid,
                            Ball ball,
                            HashMap<Integer, Robot> allies,
                            HashMap<Integer, Robot> foes) {
        super(module);
        this.ally = ally;
        this.pos = pos;
        this.orientation = 0;
        this.facePos = facePos;
        this.pathfindGrid = pathfindGrid;
        this.ball = ball;
        this.allies = allies;
        this.foes = foes;
    }

    @Override
    public void run() {
        Vector2d offset;
        if (facePos == null)
            offset = new Vector2d((float) Math.cos(orientation), (float) Math.sin(orientation));
        else
            offset = facePos.sub(pos).norm();
        offset = offset.scale(objectConfig.objectToCameraFactor * objectConfig.ballRadius
                + objectConfig.objectToCameraFactor * objectConfig.robotRadius);
        Vector2d allyTargetPos = pos.sub(offset);

        PathToPointSkill pathToPointSkill;
        if (facePos == null)
            pathToPointSkill = new PathToPointSkill(module,
                    ally,
                    allyTargetPos,
                    orientation,
                    pathfindGrid,
                    allies,
                    foes);
        else
            pathToPointSkill = new PathToPointSkill(module,
                    ally,
                    allyTargetPos,
                    facePos,
                    pathfindGrid,
                    allies,
                    foes);
        pathToPointSkill.start();
    }
}
