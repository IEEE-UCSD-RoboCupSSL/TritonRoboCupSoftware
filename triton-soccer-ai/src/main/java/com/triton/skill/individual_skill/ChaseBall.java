package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.Dribble;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.util.ProtobufUtils.getPos;
import static com.triton.util.ProtobufUtils.getVel;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class ChaseBall extends Skill {
    private final Robot ally;
    private final Ball ball;
    private final Map<Integer, Robot> allies;
    private final Map<Integer, Robot> foes;
    private final PathfindGridGroup pathfindGridGroup;

    public ChaseBall(Module module,
                     Robot ally,
                     PathfindGridGroup pathfindGridGroup,
                     Ball ball,
                     Map<Integer, Robot> allies,
                     Map<Integer, Robot> foes) {
        super(module);
        this.ally = ally;
        this.pathfindGridGroup = pathfindGridGroup;
        this.ball = ball;
        this.allies = allies;
        this.foes = foes;
    }

    @Override
    protected void execute() {
        Vector2d allyPos = getPos(ally);
        Vector2d ballPos = getPos(ball);
        Vector2d ballVel = getVel(ball);
        Vector2d targetPos = ballPos.add(ballVel.scale(0.1f));

        PathToPoint pathToPoint = new PathToPoint(module, ally, targetPos, ballPos, pathfindGridGroup);
        submitSkill(pathToPoint);

        Dribble dribble = new Dribble(module, ally, true);
        submitSkill(dribble);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }
}
