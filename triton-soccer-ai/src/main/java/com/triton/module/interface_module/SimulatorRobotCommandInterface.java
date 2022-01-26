package com.triton.module.interface_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.module.Module;
import com.triton.networking.UDP_Client;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_ROBOT_COMMAND;
import static com.triton.messaging.Exchange.AI_ROBOT_FEEDBACKS;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.RobotCommand;
import static proto.simulation.SslSimulationRobotControl.RobotControl;
import static proto.simulation.SslSimulationRobotFeedback.RobotControlResponse;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;

public class SimulatorRobotCommandInterface extends Module {
    private UDP_Client client;
    private HashMap<Integer, RobotFeedback> feedbacks;

    public SimulatorRobotCommandInterface() {
        super();
        client.start();
    }

    @Override
    protected void prepare() {
        super.prepare();
        feedbacks = new HashMap<>();

        try {
            setupClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_ROBOT_COMMAND, this::callbackRobotCommand);
    }

    private void setupClient() throws IOException {
        String allyControlAddress;
        int allyControlPort;

        switch (RuntimeConstants.team) {
            case BLUE -> {
                allyControlAddress = RuntimeConstants.networkConfig.simulationRobotControlAddressBlue;
                allyControlPort = RuntimeConstants.networkConfig.simulationRobotControlPortBlue;
            }
            case YELLOW -> {
                allyControlAddress = RuntimeConstants.networkConfig.simulationRobotControlAddressYellow;
                allyControlPort = RuntimeConstants.networkConfig.simulationRobotControlPortYellow;
            }
            default -> throw new IllegalStateException("Unexpected value: " + RuntimeConstants.team);
        }

        client = new UDP_Client(allyControlAddress, allyControlPort, this::callbackRobotControlResponse, 10);
    }


    private void callbackRobotCommand(String s, Delivery delivery) {
        RobotCommand robotCommand = (RobotCommand) simpleDeserialize(delivery.getBody());

        RobotControl.Builder robotControl = RobotControl.newBuilder();
        robotControl.addRobotCommands(robotCommand);

        client.addSend(robotControl.build().toByteArray());
    }

    private void callbackRobotControlResponse(byte[] bytes) {
        RobotControlResponse response = null;

        try {
            response = RobotControlResponse.parseFrom(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (RobotFeedback feedback : response.getFeedbackList())
            feedbacks.put(feedback.getId(), feedback);

        publish(AI_ROBOT_FEEDBACKS, feedbacks);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        client.interrupt();
    }
}
