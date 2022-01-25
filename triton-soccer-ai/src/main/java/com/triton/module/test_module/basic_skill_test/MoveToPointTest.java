package com.triton.module.test_module.basic_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.helper.Vector2d;
import com.triton.module.TestRunner;
import com.triton.skill.basic_skill.MoveToPointSkill;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;
import proto.triton.ObjectWithMetadata;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_ALLIES;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.*;

public class MoveToPointTest extends TestRunner {
    private HashMap<Integer, Robot> allies;

    private MoveToPointSkill moveToPointSkill;

    public MoveToPointTest() {
        super();
        scheduleSetupTest(0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
    }

    @Override
    protected void setupTest() {
        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();

        SslSimulationControl.TeleportRobot.Builder teleportRobot = SslSimulationControl.TeleportRobot.newBuilder();
        SslGcCommon.RobotId.Builder robotId = SslGcCommon.RobotId.newBuilder();
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

        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    private void callbackAllies(String s, Delivery delivery) {
        allies = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    @Override
    public void run() {
        if (allies == null) return;

        if (moveToPointSkill == null) {
            moveToPointSkill = new MoveToPointSkill(this, allies.get(1), new Vector2d(2000, 2000), (float) Math.PI);
            scheduleSkill(moveToPointSkill);
        } else {
            moveToPointSkill.update(allies.get(1), new Vector2d(2000, 2000), (float) Math.PI);
        }
    }
}
