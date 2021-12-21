package com.triton.module;

import com.rabbitmq.client.Delivery;
import com.triton.config.NetworkConfig;
import com.triton.networking.UDP_Client;
import com.triton.publisher_consumer.Module;
import proto.simulation.SslSimulationControl.SimulatorResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import static com.triton.config.Config.NETWORK_CONFIG;
import static com.triton.config.EasyYamlReader.readYaml;
import static com.triton.publisher_consumer.EasySerialize.standardDeserialize;
import static com.triton.publisher_consumer.Exchange.SIMULATOR_COMMAND;
import static proto.simulation.SslSimulationControl.SimulatorCommand;

public class SimulatorCommandSender extends Module {
    private UDP_Client client;
    private NetworkConfig networkConfig;

    public SimulatorCommandSender() throws IOException, TimeoutException {
        super();
        setupNetworking();
        declareExchanges();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        networkConfig = (NetworkConfig) readYaml(NETWORK_CONFIG);
    }

    private void setupNetworking() throws IOException {
        client = new UDP_Client(networkConfig.getSimulationControlAddress(),
                networkConfig.getSimulationControlPort(),
                this::consumeSimulatorResponse);
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
            command = (SimulatorCommand) standardDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (command == null) return;
        client.send(command.toByteArray());
    }

    private void consumeSimulatorResponse(DatagramPacket packet) {
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
