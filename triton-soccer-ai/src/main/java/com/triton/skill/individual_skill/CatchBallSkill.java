package com.triton.skill.individual_skill;

import com.triton.helper.Vector2d;
import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.DribbleSkill;

import java.util.HashMap;

import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class CatchBallSkill extends Skill {
    private Robot ally;

    private SSL_GeometryFieldSize field;
    private Ball ball;
    private HashMap<Integer, Robot> allies;
    private HashMap<Integer, Robot> foes;

    public CatchBallSkill(Module module,
                          Robot ally,
                          SSL_GeometryFieldSize field,
                          Ball ball,
                          HashMap<Integer, Robot> allies,
                          HashMap<Integer, Robot> foes) {
        super(module);
        update(ally, field, ball, allies, foes);
    }

    public void update(Robot ally,
                       SSL_GeometryFieldSize field,
                       Ball ball,
                       HashMap<Integer, Robot> allies,
                       HashMap<Integer, Robot> foes) {
        this.ally = ally;

        this.field = field;
        this.ball = ball;
        this.allies = allies;
        this.foes = foes;
    }

    @Override
    public void run() {
        Vector2d allyPos = new Vector2d(ally.getX(), ally.getY());
        Vector2d ballPos = new Vector2d(ball.getX(), ball.getY());
        Vector2d ballVel = new Vector2d(ball.getVx(), ball.getVy());

        Vector2d diff = allyPos.sub(ballPos);
        Vector2d offset = diff.proj(ballVel);
        Vector2d targetPos = ballPos.add(offset);

        PathToPointSkill pathToPointSkill = new PathToPointSkill(module, ally, targetPos, ballPos, field, allies, foes);
        DribbleSkill dribbleSkill = new DribbleSkill(module, ally, true);
    }
}
