package com.triton.module.test_module.individual_skill_test;

import com.triton.constant.Team;
import com.triton.helper.Vector2d;
import com.triton.module.TestModule;
import com.triton.module.ai_module.skills.individual_skills.PathToPointSkill;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.triton.helper.CreateMessage.createTeleportRobot;
import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static proto.simulation.SslSimulationControl.SimulatorControl;

public class PathToPointTest extends TestModule {
    private PathToPointSkill pathToPointSkill;

    public PathToPointTest() {
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
        SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 1, 0, -2000, 0));

        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 0, -400, 0, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 2, -200, 0, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 3, 0, 0, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 4, 200, 0, 0));
        simulatorControl.addTeleportRobot(createTeleportRobot(Team.YELLOW, 5, 400, 0, 0));
        publish(AI_BIASED_SIMULATOR_CONTROL, simulatorControl.build());
    }

    @Override
    public void run() {
        if (pathToPointSkill == null) {
            pathToPointSkill = new PathToPointSkill(1, new Vector2d(0, 3000), new Vector2d(0, 3000));
            scheduleSkill(pathToPointSkill);
        }
    }
}
