package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.TritonSoccerAI;
import com.triton.module.Module;
import proto.simulation.SslSimulationControl;
import proto.triton.AiBasicSkills;
import proto.triton.AiIndividualSkills;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.AiIndividualSkills.*;

public class ChaseBallTest extends Module {
    private ArrayList<MessagesRobocupSslDetection.SSL_DetectionBall> balls;
    private HashMap<Integer, MessagesRobocupSslDetection.SSL_DetectionRobot> allies;

    public ChaseBallTest() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_FILTERED_BIASED_BALLS, this::callbackBalls);
        declareConsume(AI_FILTERED_BIASED_ALLIES, this::callbackAllies);
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
        declarePublish(AI_INDIVIDUAL_SKILL);
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
                publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
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
        IndividualSkill.Builder chaseBallSkill = IndividualSkill.newBuilder();
        chaseBallSkill.setId(0);
        ChaseBall.Builder chaseBall = ChaseBall.newBuilder();
        chaseBallSkill.setChaseBall(chaseBall);

        try {
            publish(AI_INDIVIDUAL_SKILL, chaseBallSkill.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
