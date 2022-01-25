package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.helper.Vector2d;
import com.triton.module.TestRunner;
import com.triton.skill.individual_skill.ChaseBallSkill;
import com.triton.skill.individual_skill.PathToPointSkill;
import proto.simulation.SslSimulationControl;
import proto.triton.ObjectWithMetadata;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.Exchange.AI_FILTERED_FOES;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.ObjectWithMetadata.*;
import static proto.vision.MessagesRobocupSslGeometry.*;

public class ChaseBallTest extends TestRunner {
    private SSL_GeometryFieldSize field;
    private Ball ball;
    private HashMap<Integer, Robot> allies;
    private HashMap<Integer, Robot> foes;
    private HashMap<Integer, RobotFeedback> feedbacks;

    private HashMap<Integer, PathToPointSkill> pathToPointSkills;
    private HashMap<Integer, ChaseBallSkill> chaseBallSkills;

    public ChaseBallTest() {
        super();
        pathToPointSkills = new HashMap<>();
        chaseBallSkills = new HashMap<>();
        setupTest();
    }

    @Override
    protected void setupTest() {
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();
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
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_FIELD, this::callbackField);
        declareConsume(AI_FILTERED_BALL, this::callbackBalls);
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
        declareConsume(AI_FILTERED_FOES, this::callbackFoes);
        declareConsume(AI_ROBOT_FEEDBACKS, this::callbackFeedbacks);
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
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

    private void callbackFeedbacks(String s, Delivery delivery) {
        this.feedbacks = (HashMap<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());
    }

    @Override
    public void run() {
        if (field == null || ball == null || allies == null || foes == null) return;

        for (int id = 0; id < 6; id++) {
            if (feedbacks != null && feedbacks.containsKey(0) && feedbacks.get(0).getDribblerBallContact()) {
                System.out.println("contact");
                if (pathToPointSkills.get(id) == null) {
                    PathToPointSkill pathToPointSkill = new PathToPointSkill(this,
                            allies.get(id),
                            new Vector2d(1000, 1000),
                            (float) Math.PI,
                            field,
                            allies,
                            foes);
                    scheduleSkill(pathToPointSkill);
                    pathToPointSkills.put(id, pathToPointSkill);
                    chaseBallSkills.put(id, null);
                }
            } else {
                if (chaseBallSkills.get(id) == null) {
                    ChaseBallSkill chaseBallSkill = new ChaseBallSkill(this, allies.get(id), field, ball, allies, foes);
                    scheduleSkill(chaseBallSkill);
                    chaseBallSkills.put(id, chaseBallSkill);
                    pathToPointSkills.put(id, null);
                }
            }
        }
    }
}
