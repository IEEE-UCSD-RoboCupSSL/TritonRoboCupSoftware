package com.triton.module.interface_module;

import com.rabbitmq.client.Delivery;
import com.triton.config.NetworkConfig;
import com.triton.constant.RuntimeConstants;
import com.triton.module.Module;
import com.triton.networking.UDP_Client;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.config.ConfigPath.NETWORK_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.AI_ROBOT_COMMAND;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.RobotCommand;
import static proto.simulation.SslSimulationRobotControl.RobotControl;
import static proto.simulation.SslSimulationRobotFeedback.RobotControlResponse;

public class SimulatorRobotCommandInterface extends Module {
    private NetworkConfig networkConfig;

    private UDP_Client client;

    public SimulatorRobotCommandInterface() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        networkConfig = (NetworkConfig) readConfig(NETWORK_CONFIG);
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
        declareConsume(AI_ROBOT_COMMAND, this::callbackRobotCommand);
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

        client = new UDP_Client(allyControlAddress, allyControlPort, this::callbackRobotControlResponse, 10);
        client.start();
    }


    private void callbackRobotCommand(String s, Delivery delivery) {
        RobotCommand robotCommand;
        try {
            robotCommand = (RobotCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        RobotControl.Builder robotControl = RobotControl.newBuilder();
        robotControl.addRobotCommands(robotCommand);

        System.out.println(robotCommand);

        client.addSend(robotControl.build().toByteArray());
    }

    private void callbackRobotControlResponse(byte[] bytes) {
        try {
            RobotControlResponse robotControlResponse = RobotControlResponse.parseFrom(bytes);
            System.out.println(robotControlResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
