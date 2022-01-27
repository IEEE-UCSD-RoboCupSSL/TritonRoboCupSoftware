package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.ProgramConstants;
import com.triton.module.TestRunner;
import com.triton.skill.individual_skill.MoveToTarget;
import com.triton.util.Vector2d;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_ALLIES;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.triton.ObjectWithMetadata.Robot;

public class MoveToTargetTest extends TestRunner {
    private Map<Integer, Robot> allies;

    public MoveToTargetTest(ScheduledThreadPoolExecutor executor) {
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
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
    }

    private void callbackAllies(String s, Delivery delivery) {
        allies = (Map<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    @Override
    protected void setupTest() {
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 1, 0, 0, (float) (Math.PI / 2)));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    protected void execute() {
        if (allies == null) return;

        MoveToTarget moveToTarget = new MoveToTarget(this, allies.get(1), new Vector2d(2000, 2000), 0);
        submitSkill(moveToTarget);
    }
}
