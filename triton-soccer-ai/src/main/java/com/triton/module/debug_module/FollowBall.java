package com.triton.module.debug_module;

import com.rabbitmq.client.Delivery;
import com.triton.module.Module;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiBasicSkills.MoveToPoint;

public class FollowBall extends Module {
    private ArrayList<MessagesRobocupSslDetection.SSL_DetectionBall> balls;
    private HashMap<Integer, MessagesRobocupSslDetection.SSL_DetectionRobot> allies;

    public FollowBall() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_BALLS, this::callbackBalls);
        declareConsume(AI_BIASED_ALLIES, this::callbackAllies);
        declarePublish(AI_BASIC_SKILL);
    }

    private void callbackBalls(String s, Delivery delivery) {
        ArrayList<MessagesRobocupSslDetection.SSL_DetectionBall> balls;
        try {
            balls = (ArrayList<MessagesRobocupSslDetection.SSL_DetectionBall>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        this.balls = balls;
        createCommand();
    }

    private void callbackAllies(String s, Delivery delivery) {
        HashMap<Integer, MessagesRobocupSslDetection.SSL_DetectionRobot> allies;
        try {
            allies = (HashMap<Integer, MessagesRobocupSslDetection.SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        this.allies = allies;
        createCommand();
    }

    private void createCommand() {
        if (balls == null || allies == null) {
            return;
        }

        float avgBallX = 0;
        float avgBallY = 0;

        for (MessagesRobocupSslDetection.SSL_DetectionBall ball : balls) {
            avgBallX += ball.getX();
            avgBallY += ball.getY();
        }

        avgBallX /= balls.size();
        avgBallY /= balls.size();

        for (MessagesRobocupSslDetection.SSL_DetectionRobot ally : allies.values()) {
            float allyX = ally.getX();
            float allyY = ally.getY();

            BasicSkill.Builder basicSkill = BasicSkill.newBuilder();
            basicSkill.setId(ally.getRobotId());
            MoveToPoint.Builder moveToPoint = MoveToPoint.newBuilder();
            moveToPoint.setX(avgBallX);
            moveToPoint.setY(avgBallY);
            moveToPoint.setOrientation((float) Math.atan2(avgBallY - allyY, avgBallX - allyX));
            basicSkill.setMoveToPoint(moveToPoint);

            try {
                publish(AI_BASIC_SKILL, basicSkill.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
