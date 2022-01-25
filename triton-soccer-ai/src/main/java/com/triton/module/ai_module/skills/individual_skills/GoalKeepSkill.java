package com.triton.module.ai_module.skills.individual_skills;

import com.rabbitmq.client.Delivery;
import com.triton.helper.Vector2d;
import com.triton.module.SkillModule;
import com.triton.module.ai_module.skills.basic_skills.DribbleSkill;
import com.triton.module.ai_module.skills.basic_skills.MoveToPointSkill;

import java.io.IOException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class GoalKeepSkill extends SkillModule {
    private int allyId;

    private SSL_GeometryFieldSize field;
    private Ball ball;

    public GoalKeepSkill(int allyId) {
        super();
        this.allyId = allyId;
    }

    @Override
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
        declareConsume(AI_BIASED_FIELD, this::callbackField);
        declareConsume(AI_FILTERED_BALL, this::callbackBall);
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }

    private void callbackField(String s, Delivery delivery) {
        field = (SSL_GeometryFieldSize) simpleDeserialize(delivery.getBody());
    }

    private void callbackBall(String s, Delivery delivery) {
        ball = (Ball) simpleDeserialize(delivery.getBody());
    }

    @Override
    public void run() {
        float xMin = -1000f;
        float xMax = 1000f;
        float x = Math.min(Math.max(ball.getX(), xMin), xMax);
        float y = -field.getFieldLength() / 2f + 250f;
        Vector2d pos = new Vector2d(x, y);

        MoveToPointSkill moveToPointSkill = new MoveToPointSkill(allyId, pos, (float) (Math.PI / 2));
        DribbleSkill dribbleSkill = new DribbleSkill(allyId, true);
    }

    public void setAllyId(int allyId) {
        this.allyId = allyId;
    }
}
