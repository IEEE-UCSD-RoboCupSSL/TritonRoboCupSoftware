package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.helper.CreateMessage.createTeleportRobot;
import static com.triton.messaging.Exchange.*;
import static proto.simulation.SslSimulationControl.*;
import static proto.triton.AiIndividualSkills.IndividualSkill;
import static proto.triton.AiIndividualSkills.PathToPoint;

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
            SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();

            simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 0, 0, -2000, 0));
            simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 1, -400, 0, 0));
            simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 2, -200, 0, 0));
            simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 3, 0, 0, 0));
            simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 4, 200, 0, 0));
            simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 5, 400, 0, 0));

            publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void callbackWrapper(String s, Delivery delivery) {
        for (int i = 0; i < 1; i++) {
            IndividualSkill.Builder pathToPointSkill = IndividualSkill.newBuilder();
            pathToPointSkill.setId(i);
            PathToPoint.Builder pathToPoint = PathToPoint.newBuilder();
            pathToPoint.setX(0);
            pathToPoint.setY(3000);
            pathToPoint.setFacePoint(true);
            pathToPointSkill.setPathToPoint(pathToPoint);
            publish(AI_INDIVIDUAL_SKILL, pathToPointSkill.build());
        }
    }
}
