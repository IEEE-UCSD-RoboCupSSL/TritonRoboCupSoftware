package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.AiIndividualSkills.*;

public class ChaseBallTest extends Module {
    private HashMap<Integer, RobotFeedback> feedbacks;

    public ChaseBallTest() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_VISION_WRAPPER, this::callbackWrapper);
        declareConsume(AI_ROBOT_FEEDBACKS, this::callbackFeedbacks);

        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
        declarePublish(AI_INDIVIDUAL_SKILL);
    }

    @Override
    public void run() {
        super.run();

        SslSimulationControl.SimulatorControl.Builder simulatorControl = SslSimulationControl.SimulatorControl.newBuilder();

        SslSimulationControl.TeleportBall.Builder teleportBall = SslSimulationControl.TeleportBall.newBuilder();
        teleportBall.setX(-1000f / 1000f);
        teleportBall.setY(-1000f / 1000f);
        teleportBall.setZ(0);
        teleportBall.setVx(0);
        teleportBall.setVy(0);
        teleportBall.setVz(0);
        teleportBall.setByForce(false);
        simulatorControl.setTeleportBall(teleportBall);

        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    private void callbackWrapper(String s, Delivery delivery) {
        for (int id = 0; id < 6; id++) {
            if (feedbacks != null && feedbacks.containsKey(0) && feedbacks.get(0).getDribblerBallContact()) {
                System.out.println("contact");
                IndividualSkill.Builder pathToPointSkill = IndividualSkill.newBuilder();
                pathToPointSkill.setId(id);
                PathToPoint.Builder pathToPoint = PathToPoint.newBuilder();
                pathToPoint.setX(1000);
                pathToPoint.setY(1000);
                pathToPoint.setOrientation((float) Math.PI);
                pathToPointSkill.setPathToPoint(pathToPoint);
                publish(AI_INDIVIDUAL_SKILL, pathToPointSkill.build());
            } else {
                IndividualSkill.Builder chaseBallSkill = IndividualSkill.newBuilder();
                chaseBallSkill.setId(id);
                ChaseBall.Builder chaseBall = ChaseBall.newBuilder();
                chaseBallSkill.setChaseBall(chaseBall);
                publish(AI_INDIVIDUAL_SKILL, chaseBallSkill.build());
            }
        }
    }

    private void callbackFeedbacks(String s, Delivery delivery) {
        this.feedbacks = (HashMap<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());
    }
}
