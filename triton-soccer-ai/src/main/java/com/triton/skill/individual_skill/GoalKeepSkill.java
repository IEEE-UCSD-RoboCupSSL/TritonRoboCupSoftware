package com.triton.skill.individual_skill;

import com.rabbitmq.client.Delivery;
import com.triton.helper.Vector2d;
import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.DribbleSkill;
import com.triton.skill.basic_skill.MoveToPointSkill;

import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.*;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class GoalKeepSkill extends Skill {
    private Robot ally;

    private SSL_GeometryFieldSize field;
    private Ball ball;

    public GoalKeepSkill(Module module, Robot ally, SSL_GeometryFieldSize field, Ball ball) {
        super(module);
        update(ally, field, ball);
    }

    public void update(Robot ally, SSL_GeometryFieldSize field, Ball ball) {
        this.ally = ally;
        this.field = field;
        this.ball = ball;
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

        MoveToPointSkill moveToPointSkill = new MoveToPointSkill(module, ally, pos, (float) (Math.PI / 2));
        DribbleSkill dribbleSkill = new DribbleSkill(module, ally, true);
    }
}
