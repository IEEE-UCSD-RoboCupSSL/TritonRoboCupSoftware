package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.ProgramConstants;
import com.triton.module.TestRunner;
import com.triton.skill.basic_skill.MatchVelocity;
import com.triton.util.Vector2d;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportBall;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.ObjectWithMetadata.Robot;

public class DribbleBallTest extends TestRunner {
    private Map<Integer, Robot> allies;
    private Map<Integer, RobotFeedback> feedbacks;

    public DribbleBallTest(ScheduledThreadPoolExecutor executor) {
        super(executor);
        setupTest();
    }

    @Override
    protected void setupTest() {
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 1, 0, 0, (float) (Math.PI / 2)));
        simulatorControl.setTeleportBall(createTeleportBall(-1000f, -1000f, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    protected void execute() {
        if (allies == null) return;
        MatchVelocity matchVelocity = new MatchVelocity(this, allies.get(1), new Vector2d(0, 2), 0);
        submitSkill(matchVelocity);
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
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
        declareConsume(AI_ROBOT_FEEDBACKS, this::callbackFeedbacks);
    }

    private void callbackAllies(String s, Delivery delivery) {
        allies = (Map<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    private void callbackFeedbacks(String s, Delivery delivery) {
        feedbacks = (Map<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());
    }
}
