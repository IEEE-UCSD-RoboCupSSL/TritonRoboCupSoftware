package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.ProgramConstants;
import com.triton.module.TestRunner;
import com.triton.skill.individual_skill.GoalKeep;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_VISION_WRAPPER;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportBall;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.triton.FilteredObject.*;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class GoalKeepTest extends TestRunner {
    private FilteredWrapperPacket wrapper;

    public GoalKeepTest(ScheduledThreadPoolExecutor executor) {
        super(executor);
        scheduleSetupTest(0, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void execute() {
        if (wrapper == null) return;
        SSL_GeometryFieldSize field = wrapper.getField();
        Ball ball = wrapper.getBall();
        Map<Integer, Robot> allies = wrapper.getAlliesMap();
        Map<Integer, Robot> foes = wrapper.getFoesMap();

        GoalKeep goalKeep = new GoalKeep(this, allies.get(1), wrapper);
        submitSkill(goalKeep);

        if (allies.get(1).getHasBall())
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
        declareConsume(AI_FILTERED_VISION_WRAPPER, this::callbackWrapper);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        wrapper = (FilteredWrapperPacket) simpleDeserialize(delivery.getBody());
    }

    @Override
    protected void setupTest() {
        if (wrapper == null) return;
        SSL_GeometryFieldSize field = wrapper.getField();

        Random random = new Random();
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 1, 0, -4000f, (float) (Math.PI / 2)));
        simulatorControl.setTeleportBall(createTeleportBall(random.nextFloat(-field.getGoalWidth() / 2f,
                field.getGoalWidth() / 2f), 0, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }
}
