package com.triton.module.test_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;
import proto.triton.AiBasicSkills;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslGcCommon.*;
import static proto.simulation.SslSimulationControl.*;
import static proto.triton.AiBasicSkills.*;

public class KickTest extends Module {
    private ArrayList<MessagesRobocupSslDetection.SSL_DetectionBall> balls;
    private HashMap<Integer, MessagesRobocupSslDetection.SSL_DetectionRobot> allies;

    public KickTest() throws IOException, TimeoutException {
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
            SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();

            TeleportRobot.Builder teleportRobot = TeleportRobot.newBuilder();
            RobotId.Builder robotId = RobotId.newBuilder();
            if (RuntimeConstants.team == Team.YELLOW)
                robotId.setTeam(SslGcCommon.Team.YELLOW);
            else
                robotId.setTeam(SslGcCommon.Team.BLUE);
            robotId.setId(0);
            teleportRobot.setId(robotId);
            teleportRobot.setX(0);
            teleportRobot.setY(0);
            teleportRobot.setOrientation((float) (Math.PI / 2));
            teleportRobot.setPresent(true);
            teleportRobot.setByForce(false);
            simulatorControl.addTeleportRobot(teleportRobot);

            TeleportBall.Builder teleportBall = TeleportBall.newBuilder();
            teleportBall.setX(0);
            teleportBall.setY(0.5f);
            teleportBall.setZ(0);
            teleportBall.setVx(0);
            teleportBall.setVy(-2.0f);
            teleportBall.setVz(0);
            teleportBall.setByForce(false);
            simulatorControl.setTeleportBall(teleportBall);

            try {
                publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(2000);
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
        if (balls == null || allies == null) return;

        BasicSkill.Builder kickSkill = BasicSkill.newBuilder();
        Kick.Builder kick = Kick.newBuilder();
        kick.setChip(false);
        kickSkill.setKick(kick);

        try {
            publish(AI_BASIC_SKILL, kickSkill.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
