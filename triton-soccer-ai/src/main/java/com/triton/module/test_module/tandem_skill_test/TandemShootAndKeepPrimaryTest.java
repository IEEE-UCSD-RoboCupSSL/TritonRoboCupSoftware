package com.triton.module.test_module.tandem_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.ProgramConstants;
import com.triton.module.TestRunner;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.individual_skill.ChaseBall;
import com.triton.skill.individual_skill.GoalShoot;
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
import static com.triton.util.ObjectHelper.*;
import static com.triton.util.ProtobufUtils.createTeleportBall;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.triton.FilteredObject.*;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class TandemShootAndKeepPrimaryTest extends TestRunner {
    private PathfindGridGroup pathfindGridGroup;
    private FilteredWrapperPacket wrapper;

    public TandemShootAndKeepPrimaryTest(ScheduledThreadPoolExecutor executor) {
        super(executor, 0, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void setupTest() {
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.team, 1, 0, 3500, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.foeTeam, 1, 0, 4000, 0));
        simulatorControl.setTeleportBall(createTeleportBall(0, 3000, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    protected void execute() {
        if (wrapper == null) return;
        SSL_GeometryFieldSize field = wrapper.getField();
        Ball ball = wrapper.getBall();
        Map<Integer, Robot> allies = wrapper.getAlliesMap();

        if (isInFoeGoal(ball, field) || (!isInFoeGoal(ball, field) && !isInBounds(ball, field)))
            reset();

        int id = 1;
        Robot shooter = allies.get(id);

        float goalX = 0;
        float goalY = field.getFieldLength() / 2f;
        Vector2d goalPos = new Vector2d(goalX, goalY);

        if (pathfindGridGroup == null)
            pathfindGridGroup = new PathfindGridGroup(gameConfig.numBots, field);
        pathfindGridGroup.updateObstacles(wrapper);

        if (allies.get(id).getHasBall()) {
            Vector2d kickFrom = new Vector2d(0, 3500);
            GoalShoot goalShoot = new GoalShoot(this, shooter, kickFrom, pathfindGridGroup, wrapper);
            submitSkill(goalShoot);
        } else if (!isMovingTowardTarget(ball, goalPos, 1000, (float) Math.toRadians(30))) {
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
