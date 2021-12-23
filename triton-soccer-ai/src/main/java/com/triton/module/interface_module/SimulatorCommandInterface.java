package com.triton.module.interface_module;

import com.rabbitmq.client.Delivery;
import com.triton.config.NetworkConfig;
import com.triton.module.Module;
import com.triton.networking.UDP_Client;
import proto.simulation.SslSimulationControl.SimulatorResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import static com.triton.config.ConfigPath.NETWORK_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.AI_SIMULATOR_COMMAND;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationControl.SimulatorCommand;

public class SimulatorCommandInterface extends Module {
    private NetworkConfig networkConfig;

    private UDP_Client client;

    public SimulatorCommandInterface() throws IOException, TimeoutException {
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
        client = new UDP_Client(networkConfig.simulationControlAddress,
                networkConfig.simulationControlPort,
                this::callbackSimulatorResponse);
        client.start();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_SIMULATOR_COMMAND, this::callbackSimulatorCommand);
    }

    private void callbackSimulatorCommand(String s, Delivery delivery) {
        SimulatorCommand command;
        try {
            command = (SimulatorCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        client.addSend(command.toByteArray());
    }

    private void callbackSimulatorResponse(byte[] bytes) {
        try {
            SimulatorResponse response = SimulatorResponse.parseFrom(bytes);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
