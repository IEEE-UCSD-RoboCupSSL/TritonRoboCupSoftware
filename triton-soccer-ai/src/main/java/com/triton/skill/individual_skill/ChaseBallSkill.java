package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.DribbleSkill;
import com.triton.util.Vector2d;

import java.util.HashMap;

import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class ChaseBallSkill extends Skill {
    private final Robot ally;
    private final SSL_GeometryFieldSize field;
    private final Ball ball;
    private final HashMap<Integer, Robot> allies;
    private final HashMap<Integer, Robot> foes;

    public ChaseBallSkill(Module module,
                          Robot ally,
                          SSL_GeometryFieldSize field,
                          Ball ball,
                          HashMap<Integer, Robot> allies,
                          HashMap<Integer, Robot> foes) {
        super(module);
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
        Vector2d offset = ballPos.sub(allyPos).norm().scale(100f);
        Vector2d targetPos = ballPos.add(offset);

        PathToPointSkill pathToPointSkill = new PathToPointSkill(module, ally, targetPos, ballPos, field, allies, foes);
        pathToPointSkill.start();

        DribbleSkill dribbleSkill = new DribbleSkill(module, ally, true);
        dribbleSkill.start();
    }
}
