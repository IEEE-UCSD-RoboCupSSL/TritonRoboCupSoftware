package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.module.TestRunner;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.individual_skill.ChaseBall;
import com.triton.skill.individual_skill.KickToTarget;
import com.triton.skill.individual_skill.PathToTarget;
import com.triton.util.ObjectHelper;
import com.triton.util.Vector2d;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.gameConfig;
import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ProtobufUtils.createTeleportBall;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.FilteredObject.*;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class KickTowardTargetTest extends TestRunner {
    private PathfindGridGroup pathfindGridGroup;

    private FilteredWrapperPacket wrapper;
    private Map<Integer, RobotFeedback> feedbacks;

    public KickTowardTargetTest(ScheduledThreadPoolExecutor executor) {
        super(executor);
        scheduleSetupTest(0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void setupTest() {
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();
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

        int id = 1;
        Robot ally = allies.get(1);

        if (pathfindGridGroup == null)
            pathfindGridGroup = new PathfindGridGroup(gameConfig.numBots, field);
        pathfindGridGroup.updateObstacles(allies, foes);

        if (feedbacks.get(id).getDribblerBallContact()) {
            Vector2d kickFrom = new Vector2d(1000, 1000);
            if (ObjectHelper.hasPos(ally, kickFrom, 100)) {
                KickToTarget kickToTarget = new KickToTarget(this, ally, new Vector2d(0, 0));
                submitSkill(kickToTarget);
            } else {
                PathToTarget pathToTarget = new PathToTarget(this,
                        ally,
                        kickFrom,
                        (float) Math.PI,
                        pathfindGridGroup);
                submitSkill(pathToTarget);
            }
        } else {
            ChaseBall chaseBall = new ChaseBall(this,
                    allies.get(id),
                    pathfindGridGroup,
                    ball);
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
        declareConsume(AI_ROBOT_FEEDBACKS, this::callbackFeedbacks);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        wrapper = (FilteredWrapperPacket) simpleDeserialize(delivery.getBody());
    }

    private void callbackFeedbacks(String s, Delivery delivery) {
        feedbacks = (Map<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());
    }
}
