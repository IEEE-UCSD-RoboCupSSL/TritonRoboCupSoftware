package com.triton.module.test_module.individual_skill_test;

import com.rabbitmq.client.Delivery;
import com.triton.helper.Vector2d;
import com.triton.module.Module;
import com.triton.module.TestModule;
import com.triton.module.ai_module.skills.individual_skills.ChaseBallSkill;
import com.triton.module.ai_module.skills.individual_skills.PathToPointSkill;
import proto.simulation.SslSimulationControl;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_ROBOT_FEEDBACKS;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;

public class ChaseBallTest extends TestModule {
    private HashMap<Integer, RobotFeedback> feedbacks;

    public ChaseBallTest() {
        super();
        setupTest();
    }

    @Override
    protected void setupTest() {
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

    @Override
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
        declareConsume(AI_ROBOT_FEEDBACKS, this::callbackFeedbacks);
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
    }

    private void callbackFeedbacks(String s, Delivery delivery) {
        this.feedbacks = (HashMap<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());
    }

    @Override
    public void run() {
        for (int id = 0; id < 6; id++) {
            if (feedbacks != null && feedbacks.containsKey(0) && feedbacks.get(0).getDribblerBallContact()) {
                System.out.println("contact");
                PathToPointSkill pathToPointSkill = new PathToPointSkill(id, new Vector2d(1000, 1000), (float) Math.PI);
            } else {
                ChaseBallSkill chaseBallSkill = new ChaseBallSkill(id);
            }
        }
    }
}
