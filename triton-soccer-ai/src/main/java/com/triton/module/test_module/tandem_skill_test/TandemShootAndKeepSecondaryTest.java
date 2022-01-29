package com.triton.module.test_module.tandem_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.module.TestRunner;
import com.triton.skill.individual_skill.GoalKeep;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_VISION_WRAPPER;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.FilteredObject.*;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class TandemShootAndKeepSecondaryTest extends TestRunner {
    private FilteredWrapperPacket wrapper;

    public TandemShootAndKeepSecondaryTest(ScheduledThreadPoolExecutor executor) {
        super(executor, 0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void setupTest() {
    }

    @Override
    protected void execute() {
        if (wrapper == null) return;
        Map<Integer, Robot> allies = wrapper.getAlliesMap();

        Robot keeper = allies.get(0);

        GoalKeep goalKeep = new GoalKeep(this, keeper, wrapper);
        submitSkill(goalKeep);
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
}
