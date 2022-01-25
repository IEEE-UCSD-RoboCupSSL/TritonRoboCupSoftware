package com.triton.module.ai_module.skills.individual_skills;

import com.rabbitmq.client.Delivery;
import com.triton.helper.Vector2d;
import com.triton.module.SkillModule;
import com.triton.module.ai_module.skills.basic_skills.DribbleSkill;

import java.io.IOException;
import java.util.HashMap;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class ChaseBallSkill extends SkillModule {
    private int allyId;

    private Robot ally;
    private Ball ball;

    public ChaseBallSkill(int allyId) {
        super();
        this.allyId = allyId;
    }

    @Override
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
        declareConsume(AI_FILTERED_BALL, this::callbackBall);
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }

    private void callbackBall(String s, Delivery delivery) {
        ball = (Ball) simpleDeserialize(delivery.getBody());
    }

    private void callbackAllies(String s, Delivery delivery) {
        HashMap<Integer, Robot> allies = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
        ally = allies.get(allyId);
    }

    @Override
    public void run() {
        Vector2d allyPos = new Vector2d(ally.getX(), ally.getY());
        Vector2d ballPos = new Vector2d(ball.getX(), ball.getY());
        Vector2d offset = ballPos.sub(allyPos).norm().scale(100f);
        Vector2d targetPos = ballPos.add(offset);

        PathToPointSkill pathToPointSkill = new PathToPointSkill(allyId, targetPos, ballPos);
        DribbleSkill dribbleSkill = new DribbleSkill(allyId, true);
    }

    public void setAllyId(int allyId) {
        this.allyId = allyId;
    }
}
