package com.triton.modules.interfaces;

import com.rabbitmq.client.Delivery;
import com.triton.config.NetworkConfig;
import com.triton.modules.Module;
import com.triton.networking.UDP_Client;
import proto.simulation.SslSimulationControl.SimulatorResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import static com.triton.config.Config.NETWORK_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.SIMULATOR_COMMAND;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationControl.SimulatorCommand;

public class SimulatorCommandInterface extends Module {
    private NetworkConfig networkConfig;

    private UDP_Client client;

    public SimulatorCommandInterface() throws IOException, TimeoutException {
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
        client = new UDP_Client(networkConfig.getSimulationControlAddress(),
                networkConfig.getSimulationControlPort(),
                this::callbackSimulatorResponse);
        client.start();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(SIMULATOR_COMMAND, this::callbackSimulatorCommand);
    }

    private void callbackSimulatorCommand(String s, Delivery delivery) {
        SimulatorCommand command = null;
        try {
            command = (SimulatorCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (command == null) return;

        client.send(command.toByteArray());
    }

    private void callbackSimulatorResponse(DatagramPacket packet) {
        try {
            SimulatorResponse response = parsePacket(packet);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SimulatorResponse parsePacket(DatagramPacket packet) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData(),
                packet.getOffset(),
                packet.getLength());
        SimulatorResponse error = SimulatorResponse.parseFrom(stream);
        stream.close();
        return error;
    }
}
