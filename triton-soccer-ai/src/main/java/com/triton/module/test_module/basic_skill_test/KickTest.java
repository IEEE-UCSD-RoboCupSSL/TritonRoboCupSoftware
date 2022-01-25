package com.triton.module.test_module.basic_skill_test;

import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.TestModule;
import com.triton.module.ai_module.skills.basic_skills.KickSkill;
import proto.simulation.SslGcCommon;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static proto.simulation.SslGcCommon.RobotId;
import static proto.simulation.SslSimulationControl.*;

public class KickTest extends TestModule {

    public KickTest() {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
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

    @Override
    public void run() {
        KickSkill kickSkill1 = new KickSkill(1, true, false);
    }
}
