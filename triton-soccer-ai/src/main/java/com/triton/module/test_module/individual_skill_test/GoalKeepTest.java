package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiBasicSkills.Kick;
import static proto.triton.AiIndividualSkills.GoalKeep;
import static proto.triton.AiIndividualSkills.IndividualSkill;

public class GoalKeepTest extends Module {
    HashMap<Integer, RobotFeedback> feedbacks;

    public GoalKeepTest() throws IOException, TimeoutException {
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

        Random random = new Random();

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
            teleportRobot.setY(-4000f / 1000f);
            teleportRobot.setOrientation((float) (Math.PI / 2));
            teleportRobot.setPresent(true);
            teleportRobot.setByForce(false);
            simulatorControl.addTeleportRobot(teleportRobot);

            SslSimulationControl.TeleportBall.Builder teleportBall = SslSimulationControl.TeleportBall.newBuilder();
            teleportBall.setX(random.nextFloat(-1000f / 1000f, 1000f / 1000f));
            teleportBall.setY(4000f / 1000f);
            teleportBall.setZ(0);
            teleportBall.setVx(0);
            teleportBall.setVy(-12000f / 1000f);
            teleportBall.setVz(0);
            teleportBall.setByForce(false);
            simulatorControl.setTeleportBall(teleportBall);

            publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void callbackWrapper(String s, Delivery delivery) {
        IndividualSkill.Builder goalKeepSkill = IndividualSkill.newBuilder();
        goalKeepSkill.setId(0);
        GoalKeep.Builder goalKeep = GoalKeep.newBuilder();
        goalKeepSkill.setGoalKeep(goalKeep);
        publish(AI_INDIVIDUAL_SKILL, goalKeepSkill.build());

        if (feedbacks != null && feedbacks.containsKey(0) && feedbacks.get(0).getDribblerBallContact()) {
            System.out.println("contact");
            BasicSkill.Builder kickSkill = BasicSkill.newBuilder();
            kickSkill.setId(0);
            Kick.Builder kick = Kick.newBuilder();
            kick.setChip(false);
            kickSkill.setKick(kick);
            publish(AI_BASIC_SKILL, kickSkill.build());
        }
    }

    private void callbackFeedbacks(String s, Delivery delivery) {
        HashMap<Integer, RobotFeedback> feedbacks = (HashMap<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());

        this.feedbacks = feedbacks;
    }
}
