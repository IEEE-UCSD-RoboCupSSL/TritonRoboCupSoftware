package com.triton.module.old_test_module.tandem_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.ProgramConstants;
import com.triton.module.TestRunner;
import com.triton.search.implementation.PathfindGridGroup;
import com.triton.skill.team_skill.Attack;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.ProgramConstants.gameConfig;
import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_VISION_WRAPPER;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ObjectHelper.isInBounds;
import static com.triton.util.ObjectHelper.isInFoeGoal;
import static com.triton.util.ProtobufUtils.createTeleportBall;
import static com.triton.util.ProtobufUtils.createTeleportRobot;
import static proto.triton.FilteredObject.Ball;
import static proto.triton.FilteredObject.FilteredWrapperPacket;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class TandemAttackAndDefendPrimaryTest extends TestRunner {
    private PathfindGridGroup pathfindGridGroup;
    private FilteredWrapperPacket wrapper;

    public TandemAttackAndDefendPrimaryTest(ScheduledThreadPoolExecutor executor) {
        super(executor, 0, 20000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void setupTest() {
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();

        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.foeTeam, 1, 0, 0, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.foeTeam, 2, -1000, -1000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.foeTeam, 3, -1000, 1000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.foeTeam, 4, 1000, -1000, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(ProgramConstants.foeTeam, 5, 1000, 1000, 0));

        Random random = new Random();
        simulatorControl.setTeleportBall(createTeleportBall(random.nextFloat(-500, 500), -3000, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    protected void execute() {
        if (wrapper == null) return;
        SSL_GeometryFieldSize field = wrapper.getField();
        Ball ball = wrapper.getBall();

        if (isInFoeGoal(ball, field) || (!isInFoeGoal(ball, field) && !isInBounds(ball, field)))
            reset();

        if (pathfindGridGroup == null)
            pathfindGridGroup = new PathfindGridGroup(gameConfig.numBots, field);
        pathfindGridGroup.updateObstacles(wrapper);

        Attack attack = new Attack(this, pathfindGridGroup, wrapper);
        submitSkill(attack);
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
}
