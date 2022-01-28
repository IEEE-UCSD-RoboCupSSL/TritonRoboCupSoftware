package com.triton.module.test_module.coordinated_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.ProgramConstants;
import com.triton.module.TestRunner;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.coordinated_skill.Pass;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.gameConfig;
import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportBall;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.simulation.SslSimulationControl.SimulatorControl;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PassTest extends TestRunner {
    private PathfindGridGroup pathfindGridGroup;
    private SSL_GeometryFieldSize field;
    private Ball ball;
    private Map<Integer, Robot> allies;
    private Map<Integer, Robot> foes;
    private Map<Integer, RobotFeedback> feedbacks;

    private int passerId = 1;
    private int receiverId = 2;

    public PassTest(ScheduledThreadPoolExecutor executor) {
        super(executor);
        setupTest();
//        scheduleSetupTest(0, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void setupTest() {
        SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 1, 0, 2000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 2, 0, -2000, 0));
        simulatorControl.setTeleportBall(createTeleportBall(0, 0, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
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
        declareConsume(AI_FILTERED_FOES, this::callbackFoes);
        declareConsume(AI_ROBOT_FEEDBACKS, this::callbackFeedbacks);
    }

    private void callbackField(String s, Delivery delivery) {
        field = (SSL_GeometryFieldSize) simpleDeserialize(delivery.getBody());
    }

    private void callbackBalls(String s, Delivery delivery) {
        ball = (Ball) simpleDeserialize(delivery.getBody());
    }

    private void callbackAllies(String s, Delivery delivery) {
        allies = (Map<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    private void callbackFoes(String s, Delivery delivery) {
        foes = (Map<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    private void callbackFeedbacks(String s, Delivery delivery) {
        feedbacks = (Map<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());
    }

    @Override
    protected void execute() {
        if (field == null || ball == null || allies == null || foes == null || feedbacks == null) return;

        if (pathfindGridGroup == null)
            pathfindGridGroup = new PathfindGridGroup(gameConfig.numBots, field);
        pathfindGridGroup.updateObstacles(allies, foes);

        Robot passer = allies.get(passerId);
        Robot receiver = allies.get(receiverId);

        Vector2d passFrom = new Vector2d(0, 2000);
        Vector2d passTo = new Vector2d(0, -2000);

        if (feedbacks.get(receiverId).getDribblerBallContact()) {
            int tmp = receiverId;
            receiverId = passerId;
            passerId = tmp;

            System.out.println("PASSER: " + passerId);
            System.out.println("RECEIVER: " + receiverId);
        } else {
            Pass pass = new Pass(this, passer, receiver, passFrom, passTo, pathfindGridGroup, ball, feedbacks);
            submitSkill(pass);
        }
    }
}