package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.TestRunner;
import com.triton.skill.basic_skill.KickSkill;
import com.triton.skill.individual_skill.GoalKeepSkill;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.RuntimeConstants.objectConfig;
import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class GoalKeepTest extends TestRunner {
    private MessagesRobocupSslGeometry.SSL_GeometryFieldSize field;
    private Ball ball;
    private Map<Integer, Robot> allies;
    private Map<Integer, RobotFeedback> feedbacks;

    public GoalKeepTest(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    protected void prepare() {
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_BIASED_FIELD, this::callbackField);
        declareConsume(AI_FILTERED_BALL, this::callbackBalls);
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
        declareConsume(AI_ROBOT_FEEDBACKS, this::callbackFeedbacks);
    }

    private void callbackField(String s, Delivery delivery) {
        field = (MessagesRobocupSslGeometry.SSL_GeometryFieldSize) simpleDeserialize(delivery.getBody());
    }

    private void callbackBalls(String s, Delivery delivery) {
        ball = (Ball) simpleDeserialize(delivery.getBody());
    }

    private void callbackAllies(String s, Delivery delivery) {
        allies = (Map<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    private void callbackFeedbacks(String s, Delivery delivery) {
        this.feedbacks = (Map<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());
    }

    @Override
    public void run() {
        super.run();
        scheduleSetupTest(0, 5000, TimeUnit.MILLISECONDS);
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
        teleportRobot.setY(objectConfig.cameraToObjectFactor * -4000f);
        teleportRobot.setOrientation((float) (Math.PI / 2));
        teleportRobot.setPresent(true);
        teleportRobot.setByForce(false);
        simulatorControl.addTeleportRobot(teleportRobot);

        Random random = new Random();
        SslSimulationControl.TeleportBall.Builder teleportBall = SslSimulationControl.TeleportBall.newBuilder();
        teleportBall.setX(random.nextFloat(objectConfig.cameraToObjectFactor * -1000f, objectConfig.cameraToObjectFactor * 1000f));
        teleportBall.setY(objectConfig.cameraToObjectFactor * 4000f);
        teleportBall.setZ(0);
        teleportBall.setVx(0);
        teleportBall.setVy(objectConfig.cameraToObjectFactor * -12000f);
        teleportBall.setVz(0);
        teleportBall.setByForce(false);
        simulatorControl.setTeleportBall(teleportBall);

        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    protected void execute() {
        if (field == null || ball == null || allies == null) return;

        GoalKeepSkill goalKeepSkill = new GoalKeepSkill(this, allies.get(1), field, ball);
        goalKeepSkill.start();

        if (feedbacks != null && feedbacks.containsKey(1) && feedbacks.get(1).getDribblerBallContact()) {
            System.out.println("contact");
            KickSkill kickSkill = new KickSkill(this, allies.get(1), true, false);
            kickSkill.start();
        }
    }
}
