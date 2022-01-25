package com.triton.module.test_module.basic_skill_test;

import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.helper.Vector2d;
import com.triton.module.TestModule;
import com.triton.module.ai_module.skills.basic_skills.MoveToPointSkill;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;

public class MoveToPointTest extends TestModule {
    private MoveToPointSkill moveToPointSkill;

    public MoveToPointTest() {
        super();
        scheduleSetupTest(0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
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

    @Override
    public void run() {
        Vector2d pos = new Vector2d(2000, 2000);
        if (moveToPointSkill == null) {
            moveToPointSkill = new MoveToPointSkill(1, pos, (float) Math.PI);
            scheduleSkill(moveToPointSkill);
        }
    }
}
