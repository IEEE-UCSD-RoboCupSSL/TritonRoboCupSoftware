package com.triton.module.interface_module;

import com.google.protobuf.Any;
import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import com.triton.networking.UDP_Client;
import proto.simulation.SslSimulationConfig;
import proto.simulation.SslSimulationConfig.SimulatorConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_SIMULATOR_CONFIG;
import static com.triton.messaging.Exchange.AI_SIMULATOR_CONTROL;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslGcCommon.RobotId;
import static proto.simulation.SslGcCommon.Team.BLUE;
import static proto.simulation.SslGcCommon.Team.YELLOW;
import static proto.simulation.SslSimulationControl.*;
import static sslsim.SslSimulationCustomErforceRobotSpec.RobotSpecErForce;

public class SimulatorCommandInterface extends Module {
    private UDP_Client client;

    public SimulatorCommandInterface() {
        super();
        setupSimulator();
        client.start();
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
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_SIMULATOR_CONTROL);
        declarePublish(AI_SIMULATOR_CONFIG);
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_SIMULATOR_CONTROL, this::callbackSimulatorControl);
        declareConsume(AI_SIMULATOR_CONFIG, this::callbackSimulatorConfig);
    }

    private void setupClient() throws IOException {
        client = new UDP_Client(RuntimeConstants.networkConfig.simulationCommandAddress,
                RuntimeConstants.networkConfig.simulationCommandPort,
                this::callbackSimulatorResponse,
                10);
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
            specs.setRadius(RuntimeConstants.objectConfig.robotRadius);
            specs.setHeight(RuntimeConstants.objectConfig.robotHeight);
            specs.setMass(RuntimeConstants.objectConfig.robotMass);
            specs.setMaxLinearKickSpeed(RuntimeConstants.objectConfig.robotMaxLinearKickSpeed);
            specs.setMaxChipKickSpeed(RuntimeConstants.objectConfig.robotMaxChipKickSpeed);
            specs.setCenterToDribbler(RuntimeConstants.objectConfig.robotCenterToDribbler);

            SslSimulationConfig.RobotLimits.Builder limits = SslSimulationConfig.RobotLimits.newBuilder();
            limits.setAccSpeedupAbsoluteMax(RuntimeConstants.objectConfig.robotAccSpeedupAbsoluteMax);
            limits.setAccSpeedupAngularMax(RuntimeConstants.objectConfig.robotAccSpeedupAngularMax);
            limits.setAccBrakeAbsoluteMax(RuntimeConstants.objectConfig.robotAccBrakeAbsoluteMax);
            limits.setAccBrakeAngularMax(RuntimeConstants.objectConfig.robotAccBrakeAngularMax);
            limits.setVelAbsoluteMax(RuntimeConstants.objectConfig.robotVelAbsoluteMax);
            limits.setVelAngularMax(RuntimeConstants.objectConfig.robotVelAngularMax);
            specs.setLimits(limits);

            SslSimulationConfig.RobotWheelAngles.Builder wheelAngles = SslSimulationConfig.RobotWheelAngles.newBuilder();
            wheelAngles.setFrontRight(RuntimeConstants.objectConfig.robotFrontRight);
            wheelAngles.setBackRight(RuntimeConstants.objectConfig.robotBackRight);
            wheelAngles.setBackLeft(RuntimeConstants.objectConfig.robotBackLeft);
            wheelAngles.setFrontLeft(RuntimeConstants.objectConfig.robotFrontLeft);
            specs.setWheelAngles(wheelAngles);

            RobotSpecErForce.Builder specErForce = RobotSpecErForce.newBuilder();
            specErForce.setShootRadius(RuntimeConstants.objectConfig.shootRadius);
            specErForce.setDribblerHeight(RuntimeConstants.objectConfig.dribblerHeight);
            specErForce.setDribblerWidth(RuntimeConstants.objectConfig.dribblerWidth);
            specs.addCustom(Any.pack(specErForce.build()));

            simulatorConfig.addRobotSpecs(specs);
        }

        publish(AI_SIMULATOR_CONFIG, simulatorConfig.build());
    }

    private void callbackSimulatorControl(String s, Delivery delivery) {
        SimulatorControl simulatorControl = (SimulatorControl) simpleDeserialize(delivery.getBody());

        SimulatorCommand.Builder simulatorCommand = SimulatorCommand.newBuilder();
        simulatorCommand.setControl(simulatorControl);
        client.addSend(simulatorCommand.build().toByteArray());
    }

    private void callbackSimulatorConfig(String s, Delivery delivery) {
        SimulatorConfig simulatorConfig = (SimulatorConfig) simpleDeserialize(delivery.getBody());

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

    @Override
    public void interrupt() {
        super.interrupt();
        client.interrupt();
    }
}
