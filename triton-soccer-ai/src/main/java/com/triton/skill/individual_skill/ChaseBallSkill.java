package com.triton.skill.individual_skill;

import com.triton.helper.Vector2d;
import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.DribbleSkill;

import java.util.HashMap;

import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class ChaseBallSkill extends Skill {
    private PathToPointSkill pathToPointSkill;
    private DribbleSkill dribbleSkill;

    private Robot ally;

    private SSL_GeometryFieldSize field;
    private Ball ball;
    private HashMap<Integer, Robot> allies;
    private HashMap<Integer, Robot> foes;

    public ChaseBallSkill(Module module,
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
        Vector2d offset = ballPos.sub(allyPos).norm().scale(100f);
        Vector2d targetPos = ballPos.add(offset);

        if (pathToPointSkill == null) {
            pathToPointSkill = new PathToPointSkill(module, ally, targetPos, ballPos, field, allies, foes);
            scheduleSkill(pathToPointSkill);
        } else {
            pathToPointSkill.update(ally, targetPos, ballPos, field, allies, foes);
        }

        if (dribbleSkill == null) {
            dribbleSkill = new DribbleSkill(module, ally, true);
            scheduleSkill(dribbleSkill);
        }
    }
}
