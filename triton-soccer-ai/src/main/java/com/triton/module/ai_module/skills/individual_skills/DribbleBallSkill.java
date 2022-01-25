package com.triton.module.ai_module.skills.individual_skills;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.helper.Vector2d;
import com.triton.module.SkillModule;

import java.io.IOException;
import java.util.HashMap;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class DribbleBallSkill extends SkillModule {
    private int allyId;

    private Vector2d pos;
    private float orientation;
    private Vector2d facePos;

    private Robot ally;
    private Ball ball;

    public DribbleBallSkill(int allyId, Vector2d pos, float orientation) {
        super();
        this.allyId = allyId;
        this.pos = pos;
        this.orientation = orientation;
    }

    public DribbleBallSkill(int allyId, Vector2d pos, Vector2d facePos) {
        this.allyId = allyId;
        this.pos = pos;
        this.facePos = facePos;
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
        Vector2d offset;
        if (facePos == null)
            offset = new Vector2d((float) Math.cos(orientation), (float) Math.sin(orientation));
        else
            offset = facePos.sub(pos).norm();
        offset = offset.scale(RuntimeConstants.objectConfig.ballRadius / 1000f + RuntimeConstants.objectConfig.robotRadius / 1000f);
        Vector2d allyTargetPos = pos.sub(offset);

        PathToPointSkill pathToPointSkill;
        if (facePos == null)
            pathToPointSkill = new PathToPointSkill(allyId, allyTargetPos, orientation);
        else
            pathToPointSkill = new PathToPointSkill(allyId, allyTargetPos, facePos);
    }

    public void setAllyId(int allyId) {
        this.allyId = allyId;
    }

    public void setPos(Vector2d pos) {
        this.pos = pos;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
        this.facePos = null;
    }

    public void setFacePos(Vector2d facePos) {
        this.facePos = facePos;
    }
}
