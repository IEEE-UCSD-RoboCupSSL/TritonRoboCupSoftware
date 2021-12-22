package com.triton.module;

import com.triton.config.NetworkConfig;
import com.triton.networking.UDP_MulticastReceiver;
import com.triton.messaging.Module;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.TimeoutException;

import static com.triton.config.Config.NETWORK_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.RAW_WRAPPER_PACKAGE;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class VisionInterface extends Module {
    private NetworkConfig networkConfig;

    private UDP_MulticastReceiver receiver;

    public VisionInterface() throws IOException, TimeoutException {
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
            setupClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    private void setupClient() throws IOException {
        // Setup a multicast receiver
        receiver = new UDP_MulticastReceiver(networkConfig.getCameraAddress(),
                networkConfig.getCameraOutputPort(),
                this::processPacket);
        receiver.start();
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
