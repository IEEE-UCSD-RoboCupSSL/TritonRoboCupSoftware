package com.triton.module.test_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;
import proto.triton.AiIndividualSkills;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiBasicSkills.MoveToPoint;
import static proto.triton.AiIndividualSkills.*;

public class PathToPointTest extends Module {
    private ArrayList<MessagesRobocupSslDetection.SSL_DetectionBall> balls;
    private HashMap<Integer, MessagesRobocupSslDetection.SSL_DetectionRobot> allies;

    public PathToPointTest() throws IOException, TimeoutException {
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
            robotId.setId(0);
            teleportRobot.setId(robotId);
            teleportRobot.setX(0);
            teleportRobot.setY(0);
            teleportRobot.setOrientation((float) (Math.PI / 2));
            teleportRobot.setPresent(true);
            teleportRobot.setByForce(false);
            simulatorControl.addTeleportRobot(teleportRobot);

            try {
                publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void callbackWrapper(String s, Delivery delivery) {
        IndividualSkill.Builder pathToPointSkill = IndividualSkill.newBuilder();

        PathToPoint.Builder pathToPoint = PathToPoint.newBuilder();
        pathToPoint.setX(1000);
        pathToPoint.setY(1000);
        pathToPoint.setFacePoint(true);
        pathToPointSkill.setPathToPoint(pathToPoint);

        try {
            publish(AI_INDIVIDUAL_SKILL, pathToPointSkill.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
