package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.AiIndividualSkills.*;

public class DribbleBallTest extends Module {
    private HashMap<Integer, RobotFeedback> feedbacks;

    public DribbleBallTest() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_VISION_WRAPPER, this::callbackWrapper);
        declareConsume(AI_ROBOT_FEEDBACKS, this::callbackFeedbacks);

        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
        declarePublish(AI_INDIVIDUAL_SKILL);
    }

    @Override
    public void run() {
        super.run();

        while (!isInterrupted()) {
            SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();

            SslSimulationControl.TeleportRobot.Builder teleportRobot = SslSimulationControl.TeleportRobot.newBuilder();
            SslGcCommon.RobotId.Builder robotId = SslGcCommon.RobotId.newBuilder();
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

            SslSimulationControl.TeleportBall.Builder teleportBall = SslSimulationControl.TeleportBall.newBuilder();
            teleportBall.setX(-1000f / 1000f);
            teleportBall.setY(-1000f / 1000f);
            teleportBall.setZ(0);
            teleportBall.setVx(0);
            teleportBall.setVy(0);
            teleportBall.setVz(0);
            teleportBall.setByForce(false);
            simulatorControl.setTeleportBall(teleportBall);

            publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void callbackWrapper(String s, Delivery delivery) {
        if (feedbacks != null && feedbacks.containsKey(0) && feedbacks.get(0).getDribblerBallContact()) {
            System.out.println("contact");
            IndividualSkill.Builder dribbleBallSkill = IndividualSkill.newBuilder();
            dribbleBallSkill.setId(0);
            DribbleBall.Builder dribbleBall = DribbleBall.newBuilder();
            dribbleBall.setX(1000);
            dribbleBall.setY(1000);
            dribbleBall.setFaceX(0);
            dribbleBall.setFaceY(0);
            dribbleBall.setFacePoint(true);
            dribbleBallSkill.setDribbleBall(dribbleBall);
            publish(AI_INDIVIDUAL_SKILL, dribbleBallSkill.build());
        } else {
            IndividualSkill.Builder chaseBallSkill = IndividualSkill.newBuilder();
            chaseBallSkill.setId(0);
            ChaseBall.Builder chaseBall = ChaseBall.newBuilder();
            chaseBallSkill.setChaseBall(chaseBall);
            publish(AI_INDIVIDUAL_SKILL, chaseBallSkill.build());
        }
    }

    private void callbackFeedbacks(String s, Delivery delivery) {
        this.feedbacks = (HashMap<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());
    }
}
