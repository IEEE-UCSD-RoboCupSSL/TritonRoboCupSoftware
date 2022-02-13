package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.Team;
import com.triton.module.TestRunner;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.individual_skill.PathToTarget;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.gameConfig;
import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_VISION_WRAPPER;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.simulation.SslSimulationControl.SimulatorControl;
import static proto.triton.FilteredObject.FilteredWrapperPacket;
import static proto.triton.FilteredObject.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class PathToTargetTest extends TestRunner {
    private PathfindGridGroup pathfindGridGroup;
    private FilteredWrapperPacket wrapper;

    public PathToTargetTest(ScheduledThreadPoolExecutor executor) {
        super(executor, 0, 5000, TimeUnit.MILLISECONDS);
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
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 0, -300, -2000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 1, 0, -2000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 2, 300, -2000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 3, -300, 2000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 4, 0, 2000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 5, 300, 2000, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    protected void execute() {
        if (wrapper == null) return;
        SSL_GeometryFieldSize field = wrapper.getField();
        Map<Integer, Robot> allies = wrapper.getAlliesMap();

        if (pathfindGridGroup == null)
            pathfindGridGroup = new PathfindGridGroup(gameConfig.numBots, field);
        pathfindGridGroup.updateObstacles(wrapper);

        for (int id = 0; id < gameConfig.numBots; id++) {
            PathToTarget pathToTarget = switch (id) {
                case 0 -> {
                    yield new PathToTarget(this,
                            allies.get(id),
                            new Vector2d(-300, 2000),
                            new Vector2d(-300, 2000),
                            pathfindGridGroup);
                }
                case 1 -> {
                    yield new PathToTarget(this,
                            allies.get(id),
                            new Vector2d(0, 2000),
                            new Vector2d(0, 2000),
                            pathfindGridGroup);
                }
                case 2 -> {
                    yield new PathToTarget(this,
                            allies.get(id),
                            new Vector2d(300, 2000),
                            new Vector2d(300, 2000),
                            pathfindGridGroup);
                }
                case 3 -> {
                    yield new PathToTarget(this,
                            allies.get(id),
                            new Vector2d(-300, -2000),
                            new Vector2d(-300, -2000),
                            pathfindGridGroup);
                }
                case 4 -> {
                    yield new PathToTarget(this,
                            allies.get(id),
                            new Vector2d(0, -2000),
                            new Vector2d(0, -2000),
                            pathfindGridGroup);
                }
                default -> {
                    yield new PathToTarget(this,
                            allies.get(id),
                            new Vector2d(300, -2000),
                            new Vector2d(300, -2000),
                            pathfindGridGroup);
                }
            };
            submitSkill(pathToTarget);
        }
    }
}
