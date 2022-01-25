package com.triton.skill.individual_skill;

import com.triton.constant.RuntimeConstants;
import com.triton.util.Vector2d;
import com.triton.module.Module;
import com.triton.skill.Skill;

import java.util.HashMap;

import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class DribbleBallSkill extends Skill {
    private Robot ally;
    private Vector2d pos;
    private float orientation;
    private Vector2d facePos;

    private SSL_GeometryFieldSize field;
    private Ball ball;
    private HashMap<Integer, Robot> allies;
    private HashMap<Integer, Robot> foes;

    public DribbleBallSkill(Module module,
                            Robot ally,
                            Vector2d pos,
                            float orientation,
                            SSL_GeometryFieldSize field,
                            Ball ball,
                            HashMap<Integer, Robot> allies,
                            HashMap<Integer, Robot> foes) {
        super(module);
        update(ally, pos, orientation, field, ball, allies, foes);
    }

    public DribbleBallSkill(Module module,
                            Robot ally,
                            Vector2d pos,
                            Vector2d facePos,
                            SSL_GeometryFieldSize field,
                            Ball ball,
                            HashMap<Integer, Robot> allies,
                            HashMap<Integer, Robot> foes) {
        super(module);
        update(ally, pos, facePos, field, ball, allies, foes);
    }

    private void update(Robot ally,
                        Vector2d pos,
                        float orientation,
                        SSL_GeometryFieldSize field,
                        Ball ball,
                        HashMap<Integer, Robot> allies,
                        HashMap<Integer, Robot> foes) {
        this.ally = ally;
        this.pos = pos;
        this.orientation = orientation;

        this.field = field;
        this.ball = ball;
        this.allies = allies;
        this.foes = foes;
    }

    private void update(Robot ally,
                        Vector2d pos,
                        Vector2d facePos,
                        SSL_GeometryFieldSize field,
                        Ball ball,
                        HashMap<Integer, Robot> allies,
                        HashMap<Integer, Robot> foes) {
        this.ally = ally;
        this.pos = pos;
        this.facePos = facePos;

        this.field = field;
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
        offset = offset.scale(RuntimeConstants.objectConfig.ballRadius / 1000f + RuntimeConstants.objectConfig.robotRadius / 1000f);
        Vector2d allyTargetPos = pos.sub(offset);

        PathToPointSkill pathToPointSkill;
        if (facePos == null)
            pathToPointSkill = new PathToPointSkill(module, ally, allyTargetPos, orientation, field, allies, foes);
        else
            pathToPointSkill = new PathToPointSkill(module, ally, allyTargetPos, facePos, field, allies, foes);
        pathToPointSkill.start();
    }
}
