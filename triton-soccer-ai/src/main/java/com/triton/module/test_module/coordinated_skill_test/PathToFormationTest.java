package com.triton.module.test_module.coordinated_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.module.TestRunner;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.coordinated_skill.PathToFormation;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.gameConfig;
import static com.triton.constant.ProgramConstants.team;
import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.simulation.SslSimulationControl.SimulatorControl;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathToFormationTest extends TestRunner {
    private PathfindGridGroup pathfindGridGroup;
    private SSL_GeometryFieldSize field;
    private Ball ball;
    private Map<Integer, Robot> allies;
    private Map<Integer, Robot> foes;

    public PathToFormationTest(ScheduledThreadPoolExecutor executor) {
        super(executor);
        scheduleSetupTest(0, 5000, TimeUnit.MILLISECONDS);
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

    @Override
    protected void setupTest() {
        SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(team, 0, 0, -2000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(team, 1, 0, -1600, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(team, 2, 0, -1200, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(team, 3, 0, 1200, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(team, 4, 0, 1600, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(team, 5, 0, 2000, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    protected void execute() {
        if (field == null || ball == null || allies == null || foes == null) return;

        if (pathfindGridGroup == null)
            pathfindGridGroup = new PathfindGridGroup(gameConfig.numBots, field);
        pathfindGridGroup.updateObstacles(allies, foes);

        HashMap<Vector2d, Float> positions = new HashMap<>();
        positions.put(new Vector2d(-1000, 0), 0f);
        positions.put(new Vector2d(-600, 0), (float) Math.PI);
        positions.put(new Vector2d(-200, 0), 0f);
        positions.put(new Vector2d(200, 0), (float) Math.PI);
        positions.put(new Vector2d(600, 0), 0f);
        positions.put(new Vector2d(1000, 0), (float) Math.PI);

        PathToFormation pathToFormation = new PathToFormation(this, positions, allies, foes, pathfindGridGroup);
        submitSkill(pathToFormation);
    }
}
