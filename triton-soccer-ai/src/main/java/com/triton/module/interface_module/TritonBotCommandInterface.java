package com.triton.module.interface_module;

import com.rabbitmq.client.Delivery;
import com.triton.config.NetworkConfig;
import com.triton.module.Module;
import com.triton.networking.UDP_Server;
import proto.triton.TritonBotInit;
import proto.triton.TritonBotInit.Init;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.config.ConfigPath.NETWORK_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.TRITON_BOT_COMMAND;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.RobotCommand;

public class TritonBotCommandInterface extends Module {
    private NetworkConfig networkConfig;

    private UDP_Server server;
    private Map<Integer, InetAddress> addressMap;
    private Map<Integer, Integer> portMap;

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

        addressMap = new HashMap<>();
        portMap = new HashMap<>();

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

    private void callbackTritonBotCommand(String s, Delivery delivery) {
        RobotCommand command;
        try {
            command = (RobotCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        server.send(command.toByteArray(), addressMap.get(command.getId()), portMap.get(command.getId()));
    }

    private void callbackTritonBotResponse(DatagramPacket packet) {
        Init init;
        try {
            init = parseInit(packet);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        addressMap.put(init.getId(), packet.getAddress());
        portMap.put(init.getId(), packet.getPort());
    }

    private Init parseInit(DatagramPacket packet) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData(),
                packet.getOffset(),
                packet.getLength());
        TritonBotInit.Init init =
                TritonBotInit.Init.parseFrom(stream);
        stream.close();
        return init;
    }
}
