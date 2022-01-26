package com.triton.module.test_module.basic_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.Team;
import com.triton.module.TestRunner;
import com.triton.skill.basic_skill.DribbleSkill;
import com.triton.skill.basic_skill.MatchVelocitySkill;
import com.triton.util.Vector2d;
import proto.simulation.SslGcCommon;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.constant.RuntimeConstants.objectConfig;
import static com.triton.constant.RuntimeConstants.team;
import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_FILTERED_ALLIES;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslGcCommon.RobotId;
import static proto.simulation.SslSimulationControl.*;
import static proto.triton.ObjectWithMetadata.Robot;

public class DribbleTest extends TestRunner {
    private HashMap<Integer, Robot> allies;

    public DribbleTest() {
        super();
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_FILTERED_ALLIES, this::callbackAllies);
    }

    @Override
    protected void setupTest() {
        SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();

        TeleportRobot.Builder teleportRobot = TeleportRobot.newBuilder();
        RobotId.Builder robotId = RobotId.newBuilder();
        if (team == Team.YELLOW)
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
        teleportBall.setX(objectConfig.cameraToObjectFactor * 500f);
        teleportBall.setY(0);
        teleportBall.setZ(0);
        teleportBall.setVx(0);
        teleportBall.setVy(0);
        teleportBall.setVz(0);
        teleportBall.setByForce(false);
        simulatorControl.setTeleportBall(teleportBall);

        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    private void callbackAllies(String s, Delivery delivery) {
        allies = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    @Override
    public void run() {
        if (allies == null) return;

        MatchVelocitySkill matchVelocitySkill = new MatchVelocitySkill(this, allies.get(1), new Vector2d(1, 0), 0);
        matchVelocitySkill.start();

        DribbleSkill dribbleSkill = new DribbleSkill(this, allies.get(1), true);
        dribbleSkill.start();
    }
}
