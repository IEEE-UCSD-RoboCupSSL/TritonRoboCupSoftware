package com.triton.module.source_module;

import com.rabbitmq.client.Delivery;
import com.triton.module.Module;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.*;

public class FollowBall extends Module {
    private ArrayList<MessagesRobocupSslDetection.SSL_DetectionBall> balls;
    private ArrayList<MessagesRobocupSslDetection.SSL_DetectionRobot> allies;

    public FollowBall() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_BALLS, this::callbackBalls);
        declareConsume(AI_BIASED_ALLIES, this::callbackAllies);
        declarePublish(AI_BIASED_ROBOT_COMMAND);
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
        ArrayList<MessagesRobocupSslDetection.SSL_DetectionRobot> allies;
        try {
            allies = (ArrayList<MessagesRobocupSslDetection.SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
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

        for (MessagesRobocupSslDetection.SSL_DetectionRobot ally : allies) {
            float allyX = ally.getX();
            float allyY = ally.getY();

            float vx = (avgBallX - allyX) / 100;
            float vy = (avgBallY - allyY) / 100;

            RobotCommand.Builder robotCommand = RobotCommand.newBuilder();
            robotCommand.setId(ally.getRobotId());
            RobotMoveCommand.Builder moveCommand = RobotMoveCommand.newBuilder();
            MoveGlobalVelocity.Builder globalVelocity = MoveGlobalVelocity.newBuilder();
            globalVelocity.setX(vx);
            globalVelocity.setY(vy);
            globalVelocity.setAngular(0);
            moveCommand.setGlobalVelocity(globalVelocity);
            robotCommand.setMoveCommand(moveCommand);

            try {
                publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
