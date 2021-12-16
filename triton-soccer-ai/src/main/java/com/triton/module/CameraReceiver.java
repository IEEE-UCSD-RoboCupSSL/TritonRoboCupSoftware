package com.triton.module;

import com.triton.config.NetworkConfig;
import com.triton.networking.UDP_MulticastReceiver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import static com.triton.config.Config.NETWORK_CONFIG;
import static com.triton.config.EasyYamlReader.readYaml;
import static com.triton.publisher_consumer.Exchange.*;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class CameraReceiver extends Module {
    private NetworkConfig networkConfig;

    public CameraReceiver() throws IOException, TimeoutException {
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
        UDP_MulticastReceiver udpMulticastReceiver = new UDP_MulticastReceiver(networkConfig.getCameraInputPort(),
                networkConfig.getCameraInputMulticastAddress(),
                this::processPacket);
        udpMulticastReceiver.start();
    }

    /**
     * Called when a packet is received. Parses the packet and publishes it to exchanges.
     *
     * @param packet the packet received
     */
    private void processPacket(DatagramPacket packet) {
        try {
            SSL_WrapperPacket sslWrapperPacket = parsePacket(packet);
            publishWrapperPacket(sslWrapperPacket);
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
        SSL_WrapperPacket sslWrapperPacket =
                SSL_WrapperPacket.parseFrom(stream);
        stream.close();
        return sslWrapperPacket;
    }

    /**
     * Publishes an SSL_WrapperPacket to various echanges
     *
     * @param sslWrapperPacket the packet to send
     * @throws IOException
     */
    private void publishWrapperPacket(SSL_WrapperPacket sslWrapperPacket) throws IOException {
        publish(RAW_WRAPPER_PACKAGE, sslWrapperPacket);
        publish(RAW_GEOMETRY, sslWrapperPacket.getGeometry());
        publish(RAW_DETECTION, sslWrapperPacket.getDetection());
        publish(RAW_BALLS, sslWrapperPacket.getDetection().getBallsList());
        publish(RAW_ROBOTS_YELLOW, sslWrapperPacket.getDetection().getRobotsYellowList());
        publish(RAW_ROBOTS_BLUE, sslWrapperPacket.getDetection().getRobotsBlueList());
    }
}
