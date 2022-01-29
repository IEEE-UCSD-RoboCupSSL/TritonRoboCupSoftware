package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.ProgramConstants;
import com.triton.module.TestRunner;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.individual_skill.ChaseBall;
import com.triton.skill.individual_skill.KickFromPosition;
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
import static com.triton.util.ObjectHelper.isMovingTowardTarget;
import static com.triton.util.ProtobufUtils.createTeleportBall;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.simulation.SslSimulationControl.SimulatorControl;
import static proto.triton.FilteredObject.*;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class KickFromPositionTest extends TestRunner {
    private PathfindGridGroup pathfindGridGroup;
    private FilteredWrapperPacket wrapper;

    public KickFromPositionTest(ScheduledThreadPoolExecutor executor) {
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
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 1, 0, 2000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 2, 0, -2000, 0));
        simulatorControl.setTeleportBall(createTeleportBall(0, 0, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    protected void execute() {
        if (wrapper == null) return;
        SSL_GeometryFieldSize field = wrapper.getField();
        Ball ball = wrapper.getBall();
        Map<Integer, Robot> allies = wrapper.getAlliesMap();
        Map<Integer, Robot> foes = wrapper.getFoesMap();

        if (pathfindGridGroup == null)
            pathfindGridGroup = new PathfindGridGroup(gameConfig.numBots, field);
        pathfindGridGroup.updateObstacles(allies, foes);

        int passerId = 1;
        int receiverId = 2;
        Robot passer = allies.get(passerId);

        Vector2d kickFrom = new Vector2d(-1000, 2000);
        Vector2d kickTo = new Vector2d(0, -2000);

        if (allies.get(receiverId).getHasBall()) {
            System.out.println("SUCCESSFUL PASS");
        } else {
            if (allies.get(passerId).getHasBall()) {
                KickFromPosition kickFromPosition = new KickFromPosition(this, passer, kickFrom, kickTo,
                        pathfindGridGroup);
                submitSkill(kickFromPosition);
            } else {
                if (isMovingTowardTarget(ball, kickTo, 100, (float) Math.toRadians(30))) {
                    KickFromPosition kickFromPosition = new KickFromPosition(this, passer, kickFrom, kickTo,
                            pathfindGridGroup);
                    submitSkill(kickFromPosition);
                } else {
                    ChaseBall chaseBall = new ChaseBall(this, passer, pathfindGridGroup, wrapper);
                    submitSkill(chaseBall);
                }
            }
        }
    }
}
