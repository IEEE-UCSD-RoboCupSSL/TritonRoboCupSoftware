package com.triton.module.interfaces;

import com.rabbitmq.client.Delivery;
import com.triton.config.NetworkConfig;
import com.triton.messaging.Module;
import com.triton.networking.AddressPort;
import com.triton.networking.UDP_Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.config.Config.NETWORK_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.*;

public class TritonBotCommandInterface extends Module {
    private NetworkConfig networkConfig;

    private UDP_Server server;
    private Map<Integer, AddressPort> addressPorts;

    public TritonBotCommandInterface() throws IOException, TimeoutException {
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
        addressPorts = new HashMap<>();

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
        server = new UDP_Server(networkConfig.getAiTritonBotPort(), null);
        server.start();
    }

    private void callbackTritonBotCommand(String s, Delivery delivery) {
        RobotCommand command = null;
        try {
            command = (RobotCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (command == null) return;

        int id = command.getId();
        if (!addressPorts.containsKey(id)) return;

        AddressPort addressPort = addressPorts.get(id);
        InetAddress clientAddress = addressPort.getAddress();
        int clientPort = addressPort.getPort();
        server.send(command.toByteArray(), clientAddress, clientPort);
    }
}
