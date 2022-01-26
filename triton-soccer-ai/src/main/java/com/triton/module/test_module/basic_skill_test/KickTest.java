package com.triton.module.test_module.basic_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.TestRunner;
import com.triton.skill.basic_skill.Kick;
import proto.simulation.SslGcCommon;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_ALLIES;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslGcCommon.RobotId;
import static proto.simulation.SslSimulationControl.*;
import static proto.triton.ObjectWithMetadata.Robot;

public class KickTest extends TestRunner {
    private Map<Integer, Robot> allies;

    public KickTest(ScheduledThreadPoolExecutor executor) {
        super(executor);
        scheduleSetupTest(0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void execute() {
        if (allies == null) return;

        Kick kick = new Kick(this, allies.get(1), true, false);
        submitSkill(kick);
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
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
    }

    private void callbackAllies(String s, Delivery delivery) {
        allies = (Map<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    @Override
    protected void setupTest() {
        SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();

        TeleportRobot.Builder teleportRobot = TeleportRobot.newBuilder();
        RobotId.Builder robotId = RobotId.newBuilder();
        if (RuntimeConstants.team == Team.YELLOW)
            robotId.setTeam(SslGcCommon.Team.YELLOW);
        else
            robotId.setTeam(SslGcCommon.Team.BLUE);
        robotId.setId(1);
        teleportRobot.setId(robotId);
        teleportRobot.setX(0);
        teleportRobot.setY(0);
        teleportRobot.setOrientation((float) (Math.PI / 2));
        teleportRobot.setPresent(true);
        teleportRobot.setByForce(false);
        simulatorControl.addTeleportRobot(teleportRobot);

        TeleportBall.Builder teleportBall = TeleportBall.newBuilder();
        teleportBall.setX(0);
        teleportBall.setY(0.5f);
        teleportBall.setZ(0);
        teleportBall.setVx(0);
        teleportBall.setVy(-1.0f);
        teleportBall.setVz(0);
        teleportBall.setByForce(false);
        simulatorControl.setTeleportBall(teleportBall);

        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }
}
