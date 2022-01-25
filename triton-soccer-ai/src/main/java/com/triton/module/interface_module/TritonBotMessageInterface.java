package com.triton.module.interface_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.module.Module;
import com.triton.networking.UDP_Client;
import proto.triton.TritonBotCommunication.TritonBotMessage;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_ROBOT_FEEDBACKS;
import static com.triton.messaging.Exchange.AI_TRITON_BOT_MESSAGE;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;

public class TritonBotMessageInterface extends Module {
    private HashMap<Integer, UDP_Client> clientMap;
    private HashMap<Integer, RobotFeedback> feedbacks;

    public TritonBotMessageInterface() {
        super();
    }

    @Override
    protected void prepare() {
        super.prepare();

        clientMap = new HashMap<>();
        feedbacks = new HashMap<>();

        try {
            setupClients();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_TRITON_BOT_MESSAGE, this::callbackTritonBotMessage);
        declarePublish(AI_ROBOT_FEEDBACKS);
    }

    private void setupClients() throws SocketException, UnknownHostException {
        for (int id = 0; id < RuntimeConstants.gameConfig.numBots; id++) {
            String serverAddress;
            int serverPort;
            switch (RuntimeConstants.team) {
                case YELLOW -> {
                    serverAddress = RuntimeConstants.networkConfig.tritonBotAddressYellow;
                    serverPort = RuntimeConstants.networkConfig.tritonBotPortBaseYellow + id * RuntimeConstants.networkConfig.tritonBotPortIncr;
                }
                case BLUE -> {
                    serverAddress = RuntimeConstants.networkConfig.tritonBotAddressBlue;
                    serverPort = RuntimeConstants.networkConfig.tritonBotPortBaseBlue + id * RuntimeConstants.networkConfig.tritonBotPortIncr;
                }
                default -> throw new IllegalStateException("Unexpected value: " + RuntimeConstants.team);
            }

            UDP_Client client = new UDP_Client(serverAddress, serverPort, this::callbackTritonBotFeedback, 10);
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleAtFixedRate(client, 0, 10, TimeUnit.MILLISECONDS);

            clientMap.put(id, client);
        }
    }

    private void callbackTritonBotMessage(String s, Delivery delivery) {
        TritonBotMessage message = (TritonBotMessage) simpleDeserialize(delivery.getBody());

        if (clientMap.containsKey(message.getId()))
            clientMap.get(message.getId()).addSend(message.toByteArray());
    }

    private void callbackTritonBotFeedback(byte[] bytes) {
        RobotFeedback feedback = null;
        try {
            feedback = RobotFeedback.parseFrom(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (feedback == null) return;

        feedbacks.put(feedback.getId(), feedback);
        publish(AI_ROBOT_FEEDBACKS, feedbacks);
    }
}
