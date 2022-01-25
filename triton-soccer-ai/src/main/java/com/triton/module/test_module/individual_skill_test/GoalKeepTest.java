package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.TestRunner;
import com.triton.skill.basic_skill.KickSkill;
import com.triton.skill.individual_skill.GoalKeepSkill;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;
import proto.triton.ObjectWithMetadata;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.Exchange.AI_FILTERED_BALL;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.ObjectWithMetadata.*;

public class GoalKeepTest extends TestRunner {
    private MessagesRobocupSslGeometry.SSL_GeometryFieldSize field;
    private Ball ball;
    private HashMap<Integer, Robot> allies;
    private HashMap<Integer, RobotFeedback> feedbacks;

    private GoalKeepSkill goalKeepSkill;
    private KickSkill kickSkill;

    public GoalKeepTest() {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_FIELD, this::callbackField);
        declareConsume(AI_FILTERED_BALL, this::callbackBalls);
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
        declareConsume(AI_ROBOT_FEEDBACKS, this::callbackFeedbacks);
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
    }

    private void callbackField(String s, Delivery delivery) {
        field = (MessagesRobocupSslGeometry.SSL_GeometryFieldSize) simpleDeserialize(delivery.getBody());
    }

    private void callbackBalls(String s, Delivery delivery) {
        ball = (Ball) simpleDeserialize(delivery.getBody());
    }

    private void callbackAllies(String s, Delivery delivery) {
        allies = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    private void callbackFeedbacks(String s, Delivery delivery) {
        this.feedbacks = (HashMap<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());
    }

    @Override
    protected void setupTest() {
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();

        SslSimulationControl.TeleportRobot.Builder teleportRobot = SslSimulationControl.TeleportRobot.newBuilder();
        SslGcCommon.RobotId.Builder robotId = SslGcCommon.RobotId.newBuilder();
        if (RuntimeConstants.team == Team.YELLOW)
            robotId.setTeam(SslGcCommon.Team.YELLOW);
        else
            robotId.setTeam(SslGcCommon.Team.BLUE);
        robotId.setId(1);
        teleportRobot.setId(robotId);
        teleportRobot.setX(0);
        teleportRobot.setY(-4000f / 1000f);
        teleportRobot.setOrientation((float) (Math.PI / 2));
        teleportRobot.setPresent(true);
        teleportRobot.setByForce(false);
        simulatorControl.addTeleportRobot(teleportRobot);

        Random random = new Random();
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
    }

    @Override
    public void run() {
        if (field == null || ball == null || allies == null) return;

        if (goalKeepSkill == null) {
            goalKeepSkill = new GoalKeepSkill(this, allies.get(1), field, ball);
            scheduleSkill(goalKeepSkill);
        }

        if (feedbacks != null && feedbacks.containsKey(1) && feedbacks.get(1).getDribblerBallContact()) {
            System.out.println("contact");

            if (kickSkill == null) {
                kickSkill = new KickSkill(this, allies.get(1), true, false);
                scheduleSkill(kickSkill);
            }
        }
    }
}
