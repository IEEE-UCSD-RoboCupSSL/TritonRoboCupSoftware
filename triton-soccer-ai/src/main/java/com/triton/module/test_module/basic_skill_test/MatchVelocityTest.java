package com.triton.module.test_module.basic_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;
import proto.triton.AiBasicSkills;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;

public class MatchVelocityTest extends Module {

    public MatchVelocityTest() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_VISION_WRAPPER, this::callbackWrapper);
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
        declarePublish(AI_BASIC_SKILL);
    }

    @Override
    public void run() {
        super.run();

        while (!isInterrupted()) {
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

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void callbackWrapper(String s, Delivery delivery) {
        AiBasicSkills.BasicSkill.Builder matchVelocitySkill = AiBasicSkills.BasicSkill.newBuilder();
        matchVelocitySkill.setId(1);
        AiBasicSkills.MatchVelocity.Builder matchVelocity = AiBasicSkills.MatchVelocity.newBuilder();
        matchVelocity.setVx(2);
        matchVelocity.setVy(-2);
        matchVelocity.setAngular((float) (Math.PI * 2 * 1));
        matchVelocitySkill.setMatchVelocity(matchVelocity);

        publish(AI_BASIC_SKILL, matchVelocitySkill.build());
    }
}
