package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.module.TestRunner;
import com.triton.skill.individual_skill.GoalKeep;
import proto.simulation.SslSimulationControl;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportBall;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
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
        scheduleSetupTest(0, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void execute() {
        if (field == null || ball == null || allies == null || feedbacks == null) return;

        GoalKeep goalKeep = new GoalKeep(this, allies.get(1), field, ball);
        submitSkill(goalKeep);

        if (feedbacks.get(1).getDribblerBallContact())
            System.out.println("CONTACT");
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
    protected void setupTest() {
        Random random = new Random();

        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(RuntimeConstants.team, 1, 0, -4000f, (float) (Math.PI / 2)));
        simulatorControl.setTeleportBall(createTeleportBall(random.nextFloat(-field.getGoalWidth() / 2f,
                field.getGoalWidth() / 2f), 0, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }
}
