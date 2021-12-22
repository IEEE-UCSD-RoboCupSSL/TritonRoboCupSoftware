package com.triton.module;

import com.triton.config.NetworkConfig;
import com.triton.networking.UDP_MulticastClient;
import com.triton.messaging.Module;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import static com.triton.config.Config.NETWORK_CONFIG;
import static com.triton.config.EasyYamlReader.readYaml;
import static com.triton.messaging.Exchange.RAW_WRAPPER_PACKAGE;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class VisionReceiver extends Module {
    private NetworkConfig networkConfig;

    private UDP_MulticastClient client;

    public VisionReceiver() throws IOException, TimeoutException {
        super();
        setupNetworking();
        declareExchanges();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
        networkConfig = (NetworkConfig) readYaml(NETWORK_CONFIG);
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(RAW_WRAPPER_PACKAGE);
    }

    /**
     * Setup the udp multicast receiver
     *
     * @throws IOException
     */
    private void setupNetworking() throws IOException {
        // Setup a multicast receiver
        client = new UDP_MulticastClient(networkConfig.getCameraOutputAddress(),
                networkConfig.getCameraOutputPort(),
                this::processPacket);
        client.start();
    }

    /**
     * Called when a packet is received. Parses the packet and publishes it to exchanges.
     *
     * @param packet the packet received
     */
    private void processPacket(DatagramPacket packet) {
        try {
            SSL_WrapperPacket wrapperPacket = parsePacket(packet);
            publishWrapperPacket(wrapperPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses a DatagramPacket into an SSL_WrapperPacket.
     *
     * @param packet the DatagramPacket to parse
     * @return the prased SSL_WrapperPacket
     * @throws IOException
     */
    private SSL_WrapperPacket parsePacket(DatagramPacket packet) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData(),
                packet.getOffset(),
                packet.getLength());
        SSL_WrapperPacket wrapperPacket =
                SSL_WrapperPacket.parseFrom(stream);
        stream.close();
        return wrapperPacket;
    }

    /**
     * Publishes an SSL_WrapperPacket to various echanges
     *
     * @param wrapperPacket the packet to send
     * @throws IOException
     */
    private void publishWrapperPacket(SSL_WrapperPacket wrapperPacket) throws IOException {
        publish(RAW_WRAPPER_PACKAGE, wrapperPacket);
    }
}
