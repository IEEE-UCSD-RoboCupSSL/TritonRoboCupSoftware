package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.search.node2d.PathfindGrid;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.Dribble;
import com.triton.util.Vector2d;

import java.util.Map;

import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class ChaseBall extends Skill {
    private final Robot ally;
    private final Ball ball;
    private final Map<Integer, Robot> allies;
    private final Map<Integer, Robot> foes;
    private final PathfindGrid pathfindGrid;

    public ChaseBall(Module module,
                     Robot ally,
                     PathfindGrid pathfindGrid,
                     Ball ball,
                     Map<Integer, Robot> allies,
                     Map<Integer, Robot> foes) {
        super(module);
        this.ally = ally;
        this.pathfindGrid = pathfindGrid;
        this.ball = ball;
        this.allies = allies;
        this.foes = foes;
    }

    @Override
    protected void execute() {
        Vector2d allyPos = new Vector2d(ally.getX(), ally.getY());
        Vector2d ballPos = new Vector2d(ball.getX(), ball.getY());
        Vector2d offset = ballPos.sub(allyPos).norm().scale(100f);
        Vector2d targetPos = ballPos.add(offset);

        PathToPoint pathToPoint = new PathToPoint(module, ally, targetPos, ballPos, pathfindGrid, allies, foes);
        submitSkill(pathToPoint);

        Dribble dribble = new Dribble(module, ally, true);
        submitSkill(dribble);
    }
}
