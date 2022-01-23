package com.triton.module.interface_module;

import com.google.protobuf.Any;
import com.rabbitmq.client.Delivery;
import com.triton.config.NetworkConfig;
import com.triton.config.ObjectConfig;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import com.triton.networking.UDP_Client;
import proto.simulation.SslSimulationConfig;
import proto.simulation.SslSimulationConfig.SimulatorConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.config.ConfigPath.NETWORK_CONFIG;
import static com.triton.config.ConfigPath.OBJECT_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.AI_SIMULATOR_CONFIG;
import static com.triton.messaging.Exchange.AI_SIMULATOR_CONTROL;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslGcCommon.RobotId;
import static proto.simulation.SslGcCommon.Team.BLUE;
import static proto.simulation.SslGcCommon.Team.YELLOW;
import static proto.simulation.SslSimulationControl.*;
import static sslsim.SslSimulationCustomErforceRobotSpec.RobotSpecErForce;

public class SimulatorCommandInterface extends Module {
    private NetworkConfig networkConfig;
    private ObjectConfig objectConfig;

    private UDP_Client client;

    public SimulatorCommandInterface() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        networkConfig = (NetworkConfig) readConfig(NETWORK_CONFIG);
        objectConfig = (ObjectConfig) readConfig(OBJECT_CONFIG);
    }

    @Override
    protected void prepare() {
        super.prepare();

        try {
            setupClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_SIMULATOR_CONTROL, this::callbackSimulatorControl);
        declareConsume(AI_SIMULATOR_CONFIG, this::callbackSimulatorConfig);
    }

    @Override
    public void run() {
        super.run();

        setupSimulator();
    }

    private void setupClient() throws IOException {
        client = new UDP_Client(networkConfig.simulationCommandAddress,
                networkConfig.simulationCommandPort,
                this::callbackSimulatorResponse,
                10);
        client.start();
    }

    private void setupSimulator() {
        SimulatorControl.Builder simulatorControl = SimulatorControl.newBuilder();

        for (int id = 6; id < 12; id++) {
            TeleportRobot.Builder teleportRobot = TeleportRobot.newBuilder();
            RobotId.Builder robotId = RobotId.newBuilder();
            robotId.setTeam(BLUE);
            robotId.setId(id);
            teleportRobot.setId(robotId);
            teleportRobot.setPresent(false);
            simulatorControl.addTeleportRobot(teleportRobot);
        }

        for (int id = 6; id < 12; id++) {
            TeleportRobot.Builder teleportRobot = TeleportRobot.newBuilder();
            RobotId.Builder robotId = RobotId.newBuilder();
            robotId.setTeam(YELLOW);
            robotId.setId(id);
            teleportRobot.setId(robotId);
            teleportRobot.setPresent(false);
            simulatorControl.addTeleportRobot(teleportRobot);
        }

        publish(AI_SIMULATOR_CONTROL, simulatorControl.build());

        SimulatorConfig.Builder simulatorConfig = SimulatorConfig.newBuilder();
        for (int i = 0; i < 6; i++) {
            SslSimulationConfig.RobotSpecs.Builder specs = SslSimulationConfig.RobotSpecs.newBuilder();

            RobotId.Builder robotId = RobotId.newBuilder();
            if (RuntimeConstants.team == Team.YELLOW)
                robotId.setTeam(YELLOW);
            else
                robotId.setTeam(BLUE);
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

            RobotSpecErForce.Builder specErForce = RobotSpecErForce.newBuilder();
            specErForce.setShootRadius(objectConfig.shootRadius);
            specErForce.setDribblerHeight(objectConfig.dribblerHeight);
            specErForce.setDribblerWidth(objectConfig.dribblerWidth);
            specs.addCustom(Any.pack(specErForce.build()));

            simulatorConfig.addRobotSpecs(specs);
        }

        publish(AI_SIMULATOR_CONFIG, simulatorConfig.build());
    }

    private void callbackSimulatorControl(String s, Delivery delivery) {
        SimulatorControl simulatorControl;
        simulatorControl = (SimulatorControl) simpleDeserialize(delivery.getBody());

        SimulatorCommand.Builder simulatorCommand = SimulatorCommand.newBuilder();
        simulatorCommand.setControl(simulatorControl);
        client.addSend(simulatorCommand.build().toByteArray());
    }

    private void callbackSimulatorConfig(String s, Delivery delivery) {
        SimulatorConfig simulatorConfig;
        simulatorConfig = (SimulatorConfig) simpleDeserialize(delivery.getBody());

        SimulatorCommand.Builder simulatorCommand = SimulatorCommand.newBuilder();
        simulatorCommand.setConfig(simulatorConfig);
        client.addSend(simulatorCommand.build().toByteArray());
    }

    private void callbackSimulatorResponse(byte[] bytes) {
        try {
            SimulatorResponse simulatorResponse = SimulatorResponse.parseFrom(bytes);
            System.out.println(simulatorResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
