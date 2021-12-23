package com.triton.module.interface_module;

import com.triton.config.NetworkConfig;
import com.triton.module.Module;
import com.triton.networking.UDP_MulticastReceiver;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.config.ConfigPath.NETWORK_CONFIG;
import static com.triton.config.ConfigReader.readConfig;
import static com.triton.messaging.Exchange.AI_WRAPPER;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class CameraInterface extends Module {
    private NetworkConfig networkConfig;

    private UDP_MulticastReceiver receiver;

    public CameraInterface() throws IOException, TimeoutException {
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
            setupReceiver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(AI_WRAPPER);
    }

    /**
     * Setup the udp multicast receiver
     *
     * @throws IOException
     */
    private void setupReceiver() throws IOException {
        // Setup a multicast receiver
        receiver = new UDP_MulticastReceiver(networkConfig.cameraAddress,
                networkConfig.cameraOutputPort,
                this::callbackWrapper);
        receiver.start();
    }

    /**
     * Called when a packet is received. Parses the packet and publishes it to exchanges.
     *
     * @param bytes the bytes of the packet received
     */
    private void callbackWrapper(byte[] bytes) {
        try {
            SSL_WrapperPacket wrapper = SSL_WrapperPacket.parseFrom(bytes);
            publish(AI_WRAPPER, wrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
