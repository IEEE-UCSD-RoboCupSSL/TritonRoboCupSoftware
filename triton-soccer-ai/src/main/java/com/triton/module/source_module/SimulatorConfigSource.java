package com.triton.module.source_module;

import com.triton.config.ObjectConfig;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationConfig;
import proto.simulation.SslSimulationControl.TeleportBall;
import proto.simulation.SslSimulationControl.TeleportRobot;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import static com.triton.config.ConfigPath.OBJECT_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_CONTROL;
import static com.triton.messaging.Exchange.AI_SIMULATOR_CONFIG;
import static proto.simulation.SslGcCommon.RobotId;
import static proto.simulation.SslSimulationConfig.SimulatorConfig;
import static proto.simulation.SslSimulationControl.SimulatorCommand;
import static proto.simulation.SslSimulationControl.SimulatorControl;

public class SimulatorConfigSource extends Module {
    private ObjectConfig objectConfig;

    public SimulatorConfigSource() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        objectConfig = (ObjectConfig) readConfig(OBJECT_CONFIG);
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(AI_BIASED_SIMULATOR_CONTROL);
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            SimulatorConfig.Builder simulatorConfig = SimulatorConfig.newBuilder();
            for (int i = 0; i < 6; i++) {
                SslSimulationConfig.RobotSpecs.Builder specs = SslSimulationConfig.RobotSpecs.newBuilder();

                RobotId.Builder robotId = RobotId.newBuilder();
                robotId.setTeam(SslGcCommon.Team.YELLOW);
                robotId.setId(i);
                specs.setId(robotId);

                specs.setRadius(objectConfig.robotRadius);
                specs.setHeight(objectConfig.robotHeight);
                specs.setMass(objectConfig.robotMass);
                specs.setMaxLinearKickSpeed(objectConfig.robotMaxLinearKickSpeed);
                specs.setMaxChipKickSpeed(objectConfig.robotMaxChipKickSpeed);
                specs.setCenterToDribbler(objectConfig.robotCenterToDribbler);

                SslSimulationConfig.RobotLimits.Builder limits = SslSimulationConfig.RobotLimits.newBuilder();
                limits.setAccSpeedupAbsoluteMax(objectConfig.robotAccSpeedupAbsoluteMax);
                limits.setAccSpeedupAngularMax(objectConfig.robotAccSpeedupAngularMax);
                limits.setAccBrakeAbsoluteMax(objectConfig.robotAccBrakeAbsoluteMax);
                limits.setAccBrakeAngularMax(objectConfig.robotAccBrakeAngularMax);
                limits.setVelAbsoluteMax(objectConfig.robotVelAbsoluteMax);
                limits.setVelAngularMax(objectConfig.robotVelAngularMax);
                specs.setLimits(limits);

                SslSimulationConfig.RobotWheelAngles.Builder wheelAngles = SslSimulationConfig.RobotWheelAngles.newBuilder();
                wheelAngles.setFrontRight(objectConfig.robotFrontRight);
                wheelAngles.setBackRight(objectConfig.robotBackRight);
                wheelAngles.setBackLeft(objectConfig.robotBackLeft);
                wheelAngles.setFrontLeft(objectConfig.robotFrontLeft);
                specs.setWheelAngles(wheelAngles);

                simulatorConfig.addRobotSpecs(specs);
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                publish(AI_SIMULATOR_CONFIG, simulatorConfig.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
