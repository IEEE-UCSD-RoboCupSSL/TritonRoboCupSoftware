package com.triton.module.old_test_module.basic_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.ProgramConstants;
import com.triton.module.TestRunner;
import com.triton.skill.basic_skill.MatchVelocity;
import com.triton.util.Vector2d;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_VISION_WRAPPER;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.triton.FilteredObject.FilteredWrapperPacket;
import static proto.triton.FilteredObject.Robot;

public class MatchVelocityTest extends TestRunner {
    private FilteredWrapperPacket wrapper;

    public MatchVelocityTest(ScheduledThreadPoolExecutor executor) {
        super(executor, 0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void execute() {
        if (wrapper == null) return;
        Map<Integer, Robot> allies = wrapper.getAlliesMap();

        MatchVelocity matchVelocity = new MatchVelocity(this, allies.get(1), new Vector2d(0, 2), 2f);
        submitSkill(matchVelocity);
    }

    @Override
    protected void prepare() {
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_FILTERED_VISION_WRAPPER, this::callbackWrapper);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        wrapper = (FilteredWrapperPacket) simpleDeserialize(delivery.getBody());
    }

    @Override
    protected void setupTest() {
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 1, 0, 0, (float) (Math.PI / 2)));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }
}
