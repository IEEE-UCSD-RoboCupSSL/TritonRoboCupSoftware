package com.triton.module.test_module.basic_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslGcCommon;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static proto.simulation.SslGcCommon.RobotId;
import static proto.simulation.SslSimulationControl.*;
import static proto.triton.AiBasicSkills.*;

public class DribbleTest extends Module {
    public DribbleTest() throws IOException, TimeoutException {
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
            teleportBall.setX(500f / 1000f);
            teleportBall.setY(0);
            teleportBall.setZ(0);
            teleportBall.setVx(0);
            teleportBall.setVy(0);
            teleportBall.setVz(0);
            teleportBall.setByForce(false);
            simulatorControl.setTeleportBall(teleportBall);

            publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void callbackWrapper(String s, Delivery delivery) {
        BasicSkill.Builder dribbleSkill = BasicSkill.newBuilder();
        Dribble.Builder dribble = Dribble.newBuilder();
        dribble.setDribbleOn(true);
        dribbleSkill.setDribble(dribble);

        publish(AI_BASIC_SKILL, dribbleSkill.build());

        BasicSkill.Builder matchVelocitySkill = BasicSkill.newBuilder();
        MatchVelocity.Builder matchVelocity = MatchVelocity.newBuilder();
        matchVelocity.setVx(1f);
        matchVelocity.setVy(0);
        matchVelocity.setAngular(0);
        matchVelocitySkill.setMatchVelocity(matchVelocity);

        publish(AI_BASIC_SKILL, matchVelocitySkill.build());
    }
}
