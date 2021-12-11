package com.triton.module;

import com.triton.PublisherConsumer.Module;
import com.triton.config.ConfigPaths;
import com.triton.config.NetworkConfig;
import com.triton.networking.UDP_MulticastReceiver;
import proto.vision.MessagesRobocupSslWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import static com.triton.PublisherConsumer.Exchange.*;
import static com.triton.config.ConfigPaths.NETWORK_CONFIG;
import static com.triton.utility.EasyYaml.readYaml;

public class CameraReceiver extends Module {
    public CameraReceiver() throws IOException, TimeoutException {
        super();
        setupNetworking();
    }

    public static void main(String[] args) {
        try {
            new CameraReceiver();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupRabbitMQ() throws IOException, TimeoutException {
        super.setupRabbitMQ();

        declarePublish(SSL_WRAPPER_PACKAGE_EXCHANGE);
        declarePublish(SSL_GEOMETRY_DATA_EXCHANGE);
        declarePublish(SSL_DETECTION_FRAME_EXCHANGE);
        declarePublish(SSL_DETECTION_BALLS_EXCHANGE);
        declarePublish(SSL_DETECTION_ROBOTS_YELLOW_EXCHANGE);
        declarePublish(SSL_DETECTION_ROBOTS_BLUE_EXCHANGE);
    }

    public void setupNetworking() throws IOException {
        NetworkConfig networkConfig = (NetworkConfig) readYaml(NETWORK_CONFIG.getConfigPath(), NetworkConfig.class);

        // Setup a multicast receiver
        UDP_MulticastReceiver udpMulticastReceiver = new UDP_MulticastReceiver(networkConfig.getCameraInputPort(),
                networkConfig.getCameraInputMulticastAddress(),
                this::processPacket);
        udpMulticastReceiver.start();
    }

    public void processPacket(DatagramPacket packet) {
        try {
            MessagesRobocupSslWrapper.SSL_WrapperPacket sslWrapperPacket = parsePacket(packet);
            publishWrapperPacket(sslWrapperPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MessagesRobocupSslWrapper.SSL_WrapperPacket parsePacket(DatagramPacket packet) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData(),
                packet.getOffset(),
                packet.getLength());
        MessagesRobocupSslWrapper.SSL_WrapperPacket sslWrapperPacket =
                MessagesRobocupSslWrapper.SSL_WrapperPacket.parseFrom(stream);
        stream.close();
        return sslWrapperPacket;
    }

    public void publishWrapperPacket(MessagesRobocupSslWrapper.SSL_WrapperPacket sslWrapperPacket) throws IOException {
        publish(SSL_WRAPPER_PACKAGE_EXCHANGE, sslWrapperPacket);
        publish(SSL_GEOMETRY_DATA_EXCHANGE, sslWrapperPacket.getGeometry());
        publish(SSL_DETECTION_FRAME_EXCHANGE, sslWrapperPacket.getDetection());
        publish(SSL_DETECTION_BALLS_EXCHANGE, sslWrapperPacket.getDetection().getBallsList());
        publish(SSL_DETECTION_ROBOTS_YELLOW_EXCHANGE, sslWrapperPacket.getDetection().getRobotsYellowList());
        publish(SSL_DETECTION_ROBOTS_BLUE_EXCHANGE, sslWrapperPacket.getDetection().getRobotsBlueList());
    }
}
