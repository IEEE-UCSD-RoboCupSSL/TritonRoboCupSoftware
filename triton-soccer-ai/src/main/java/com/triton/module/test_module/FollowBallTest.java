package com.triton.module.test_module;

import com.rabbitmq.client.Delivery;
import com.triton.TritonSoccerAI;
import com.triton.module.Module;
import proto.simulation.SslSimulationControl;
import proto.triton.AiBasicSkills;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;

public class FollowBallTest extends Module {
    private ArrayList<MessagesRobocupSslDetection.SSL_DetectionBall> balls;
    private HashMap<Integer, MessagesRobocupSslDetection.SSL_DetectionRobot> allies;

    public FollowBallTest() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_BALLS, this::callbackBalls);
        declareConsume(AI_BIASED_ALLIES, this::callbackAllies);
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
        declarePublish(AI_BASIC_SKILL);
    }

    @Override
    public void run() {
        super.run();

        while (!isInterrupted()) {
            SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();

            SslSimulationControl.TeleportBall.Builder teleportBall = SslSimulationControl.TeleportBall.newBuilder();
            teleportBall.setX(0);
            teleportBall.setY(0);
            teleportBall.setZ(0);
            teleportBall.setVx(0);
            teleportBall.setVy(0);
            teleportBall.setVz(0);
            teleportBall.setByForce(false);
            simulatorControl.setTeleportBall(teleportBall);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

            AiBasicSkills.BasicSkill.Builder basicSkill = AiBasicSkills.BasicSkill.newBuilder();
            basicSkill.setId(ally.getRobotId());
            AiBasicSkills.MoveToPoint.Builder moveToPoint = AiBasicSkills.MoveToPoint.newBuilder();
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
