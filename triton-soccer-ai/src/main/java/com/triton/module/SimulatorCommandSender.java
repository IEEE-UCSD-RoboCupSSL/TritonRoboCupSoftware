package com.triton.module;

import com.triton.config.NetworkConfig;
import com.triton.networking.UDP_Client;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import static com.triton.config.Config.NETWORK_CONFIG;
import static com.triton.config.EasyYamlReader.readYaml;
import static com.triton.publisher_consumer.Exchange.SIMULATOR_COMMAND;
import static proto.simulation.SslSimulationControl.SimulatorCommand;

public class SimulatorCommandSender extends Module {
    private NetworkConfig networkConfig;

    private UDP_Client udpClient;

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

    private void setupNetworking() throws SocketException, UnknownHostException {
        udpClient = new UDP_Client(networkConfig.getSimulationControlAddress(),
                networkConfig.getSimulationControlPort());
        udpClient.start();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(SIMULATOR_COMMAND, this::consumeSimulatorCommand);
    }

    private void consumeSimulatorCommand(Object o) {
        if (o == null) return;
        SimulatorCommand command = (SimulatorCommand) o;
        udpClient.addSend(command.toByteArray());
    }
}
