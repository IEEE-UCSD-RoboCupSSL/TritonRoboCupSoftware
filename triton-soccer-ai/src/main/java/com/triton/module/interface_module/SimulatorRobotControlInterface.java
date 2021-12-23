package com.triton.module.interface_module;

import com.rabbitmq.client.Delivery;
import com.triton.config.NetworkConfig;
import com.triton.constant.RuntimeConstants;
import com.triton.module.Module;
import com.triton.networking.UDP_Client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import static com.triton.config.ConfigPath.NETWORK_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.AI_ROBOT_CONTROL;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.RobotControl;
import static proto.simulation.SslSimulationRobotFeedback.RobotControlResponse;

public class SimulatorRobotControlInterface extends Module {
    private NetworkConfig networkConfig;

    private UDP_Client client;

    public SimulatorRobotControlInterface() throws IOException, TimeoutException {
        super();
        setupClient();
        declareExchanges();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        networkConfig = (NetworkConfig) readConfig(NETWORK_CONFIG);
    }

    private void setupClient() throws IOException {
        String allyControlAddress;
        int allyControlPort;

        switch (RuntimeConstants.team) {
            case BLUE -> {
                allyControlAddress = networkConfig.simulationRobotControlAddressBlue;
                allyControlPort = networkConfig.simulationRobotControlPortBlue;
            }
            case YELLOW -> {
                allyControlAddress = networkConfig.simulationRobotControlAddressYellow;
                allyControlPort = networkConfig.simulationRobotControlPortYellow;
            }
            default -> throw new IllegalStateException("Unexpected value: " + RuntimeConstants.team);
        }

        client = new UDP_Client(allyControlAddress, allyControlPort, this::callbackRobotControlResponse);
        client.start();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_ROBOT_CONTROL, this::callbackRobotControl);
    }

    private void callbackRobotControl(String s, Delivery delivery) {
        RobotControl robotControl;
        try {
            robotControl = (RobotControl) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        client.addSend(robotControl.toByteArray());
    }

    private void callbackRobotControlResponse(byte[] bytes) {
        try {
            RobotControlResponse response = RobotControlResponse.parseFrom(bytes);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
