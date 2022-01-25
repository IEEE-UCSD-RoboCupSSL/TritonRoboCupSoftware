package com.triton.module.ai_module.skills.basic_skills;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.helper.PIDControl;
import com.triton.helper.Vector2d;
import com.triton.module.SkillModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static com.triton.messaging.Exchange.AI_FILTERED_ALLIES;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.Robot;

public class MoveToPointSkill extends SkillModule {
    private int allyId;
    private Vector2d pos;
    private float orientation;

    private final PIDControl pidControlX;
    private final PIDControl pidControlY;
    private final PIDControl pidControlOrientation;

    private Robot ally;
    private MatchVelocitySkill matchVelocitySkill;

    public MoveToPointSkill(int allyId, Vector2d pos, float orientation) {
        super();
        this.allyId = allyId;
        this.pos = pos;
        this.orientation = orientation;

        pidControlX = new PIDControl(RuntimeConstants.aiConfig.kpPos,
                RuntimeConstants.aiConfig.kiPos,
                RuntimeConstants.aiConfig.kdPos);
        pidControlY = new PIDControl(RuntimeConstants.aiConfig.kpPos,
                RuntimeConstants.aiConfig.kiPos,
                RuntimeConstants.aiConfig.kdPos);
        pidControlOrientation = new PIDControl(RuntimeConstants.aiConfig.kpOrientation,
                RuntimeConstants.aiConfig.kiOrientation,
                RuntimeConstants.aiConfig.kdOrientation);
    }

    @Override
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }

    private void callbackAllies(String s, Delivery delivery) {
        HashMap<Integer, Robot> allies = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
        ally = allies.get(allyId);
    }

    @Override
    public void run() {
        if (ally == null) return;

        long timestamp = System.currentTimeMillis();
        float velX = pidControlX.compute(pos.x, ally.getX(), timestamp);
        float velY = pidControlY.compute(pos.y, ally.getY(), timestamp);
        float angular = pidControlOrientation.compute(orientation, ally.getOrientation(), timestamp);
        Vector2d vel = new Vector2d(velX, velY);

        if (matchVelocitySkill == null) {
            matchVelocitySkill = new MatchVelocitySkill(allyId, vel, angular);
            scheduleSkill(matchVelocitySkill);
        } else {
            matchVelocitySkill.setAllyId(allyId);
            matchVelocitySkill.setVel(vel);
            matchVelocitySkill.setAngular(angular);
        }
    }

    public void setAllyId(int allyId) {
        this.allyId = allyId;
    }

    public void setPos(Vector2d pos) {
        this.pos = pos;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }
}
