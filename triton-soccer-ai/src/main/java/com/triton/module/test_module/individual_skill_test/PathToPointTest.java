package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.TestRunner;
import com.triton.skill.individual_skill.PathToPointSkill;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.CreateMessage.createTeleportRobot;
import static proto.simulation.SslSimulationControl.SimulatorControl;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathToPointTest extends TestRunner {
    private SSL_GeometryFieldSize field;
    private Ball ball;
    private HashMap<Integer, Robot> allies;
    private HashMap<Integer, Robot> foes;

    public PathToPointTest() {
        super();
        scheduleSetupTest(0, 10000, TimeUnit.MILLISECONDS);
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
        allies = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    private void callbackFoes(String s, Delivery delivery) {
        foes = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    @Override
    protected void setupTest() {
        SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 1, 0, -2000, 0));

        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 0, -600, 0, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 2, -300, 0, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 3, 0, 0, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 4, 300, 0, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 5, 600, 0, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    public void run() {
        if (field == null || ball == null || allies == null || foes == null) return;

        for (int id = 0; id < RuntimeConstants.gameConfig.numBots; id++) {
            PathToPointSkill pathToPointSkill;
            if (id == 1) {
                pathToPointSkill = new PathToPointSkill(this,
                        allies.get(id),
                        new Vector2d(0, 2000),
                        new Vector2d(0, 2000),
                        field,
                        allies,
                        foes);
            } else {
                pathToPointSkill = new PathToPointSkill(this,
                        allies.get(id),
                        new Vector2d(0, -2000),
                        new Vector2d(0, -2000),
                        field,
                        allies,
                        foes);
            }
            pathToPointSkill.start();
        }
    }
}
