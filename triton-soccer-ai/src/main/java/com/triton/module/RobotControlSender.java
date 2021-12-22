package com.triton.module;

import com.rabbitmq.client.Delivery;
import com.triton.TritonSoccerAI;
import com.triton.config.NetworkConfig;
import com.triton.networking.UDP_Client;
import com.triton.messaging.Module;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import static com.triton.config.Config.NETWORK_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.messaging.Exchange.ROBOT_CONTROL;
import static proto.simulation.SslSimulationRobotControl.RobotControl;
import static proto.simulation.SslSimulationRobotFeedback.RobotControlResponse;

public class RobotControlSender extends Module {
    private UDP_Client client;
    private NetworkConfig networkConfig;

    public RobotControlSender() throws IOException, TimeoutException {
        super();
        setupNetworking();
        declareExchanges();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        networkConfig = (NetworkConfig) readConfig(NETWORK_CONFIG);
    }

    private void setupNetworking() throws IOException {
        String allyControlAddress;
        int allyControlPort;

        switch (TritonSoccerAI.getTeam()) {
            case BLUE -> {
                allyControlAddress = networkConfig.getSimulationRobotControlBlueAddress();
                allyControlPort = networkConfig.getSimulationRobotControlBluePort();
            }
            case YELLOW -> {
                allyControlAddress = networkConfig.getSimulationRobotControlYellowAddress();
                allyControlPort = networkConfig.getSimulationRobotControlYellowPort();
            }
            default -> throw new IllegalStateException("Unexpected value: " + TritonSoccerAI.getTeam());
        }

        client = new UDP_Client(allyControlAddress, allyControlPort, this::consumeRobotControlResponse);
        client.start();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(ROBOT_CONTROL, this::callbackRobotControl);
    }

    private void callbackRobotControl(String s, Delivery delivery) {
        RobotControl robotControl = null;
        try {
            robotControl = (RobotControl) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (robotControl == null) return;
        client.send(robotControl.toByteArray());
    }

    private void consumeRobotControlResponse(DatagramPacket packet) {
        try {
            RobotControlResponse response = parsePacket(packet);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RobotControlResponse parsePacket(DatagramPacket packet) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData(),
                packet.getOffset(),
                packet.getLength());
        RobotControlResponse response = RobotControlResponse.parseFrom(stream);
        stream.close();
        return response;
    }
}
