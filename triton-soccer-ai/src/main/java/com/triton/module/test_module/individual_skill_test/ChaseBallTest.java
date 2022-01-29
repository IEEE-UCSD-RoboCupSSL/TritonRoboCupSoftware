package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.module.TestRunner;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.individual_skill.ChaseBall;
import com.triton.skill.individual_skill.PathToTarget;
import com.triton.util.Vector2d;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.gameConfig;
import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_VISION_WRAPPER;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportBall;
import static proto.triton.FilteredObject.*;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class ChaseBallTest extends TestRunner {
    private PathfindGridGroup pathfindGridGroup;
    private FilteredWrapperPacket wrapper;

    public ChaseBallTest(ScheduledThreadPoolExecutor executor) {
        super(executor, 0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void setupTest() {
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();
        simulatorControl.setTeleportBall(createTeleportBall(-1000f, -1000f, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    protected void execute() {
        if (wrapper == null) return;
        SSL_GeometryFieldSize field = wrapper.getField();
        Map<Integer, Robot> allies = wrapper.getAlliesMap();

        int id = 1;
        if (pathfindGridGroup == null)
            pathfindGridGroup = new PathfindGridGroup(gameConfig.numBots, field);
        pathfindGridGroup.updateObstacles(wrapper);

        if (allies.get(id).getHasBall()) {
            System.out.println("CONTACT");
            PathToTarget pathToTarget = new PathToTarget(this,
                    allies.get(id),
                    new Vector2d(1000, 1000),
                    (float) Math.PI,
                    pathfindGridGroup);
            submitSkill(pathToTarget);
        } else {
            ChaseBall chaseBall = new ChaseBall(this,
                    allies.get(id),
                    wrapper,
                    pathfindGridGroup);
            submitSkill(chaseBall);
        }
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
