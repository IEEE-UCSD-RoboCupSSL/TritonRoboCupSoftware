package com.triton.module;

import com.rabbitmq.client.Delivery;
import com.triton.config.NetworkConfig;
import com.triton.messaging.Module;
import com.triton.networking.UDP_Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.TimeoutException;

import static com.triton.config.Config.NETWORK_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.*;

public class TritonBotInterface extends Module {
    private NetworkConfig networkConfig;

    private UDP_Server server;

    public TritonBotInterface() throws IOException, TimeoutException {
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
            setupServer();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(TRITON_BOT_COMMAND, this::callbackTritonBotCommand);
    }

    private void setupServer() throws SocketException {
        server = new UDP_Server(networkConfig.getAiTritonBotPort(), this::callbackTritonBotResponse);
        server.start();
    }

    private void callbackTritonBotResponse(DatagramPacket packet) {

    }

    private void callbackTritonBotCommand(String s, Delivery delivery) {
        RobotCommand command = null;
        try {
            command = (RobotCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (command == null) return;
        InetAddress clientAddress = null; //TODO
        int clientPort = 0;
        server.send(command.toByteArray(), clientAddress, clientPort);
    }
}
