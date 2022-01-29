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
import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_VISION_WRAPPER;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.simulation.SslSimulationControl.SimulatorControl;
import static proto.triton.ObjectWithMetadata.*;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathToFormationTest extends TestRunner {
    private PathfindGridGroup pathfindGridGroup;

    private FilteredWrapperPacket wrapper;

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
        declareConsume(AI_FILTERED_VISION_WRAPPER, this::callbackWrapper);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        wrapper = (FilteredWrapperPacket) simpleDeserialize(delivery.getBody());
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
        if (wrapper == null) return;
        SSL_GeometryFieldSize field = wrapper.getField();
        Map<Integer, Robot> allies = wrapper.getAlliesMap();
        Map<Integer, Robot> foes = wrapper.getFoesMap();

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
