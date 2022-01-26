package com.triton.module.interface_module;

import com.triton.constant.RuntimeConstants;
import com.triton.module.Module;
import com.triton.networking.UDP_MulticastReceiver;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_VISION_WRAPPER;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class CameraInterface extends Module {
    private UDP_MulticastReceiver detectionReceiver;

    public CameraInterface(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    protected void prepare() {
        try {
            setupReceiver();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_VISION_WRAPPER);
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
    }

    /**
     * Setup the udp multicast receiver
     *
     * @throws IOException
     */
    private void setupReceiver() throws IOException {
        // Setup a multicast receiver
        detectionReceiver = new UDP_MulticastReceiver(RuntimeConstants.networkConfig.visionAddress,
                RuntimeConstants.networkConfig.visionDetectionPort,
                this::callbackWrapper);
    }

    /**
     * Called when a packet is received. Parses the packet and publishes it to exchanges.
     *
     * @param bytes the bytes of the packet received
     */
    private void callbackWrapper(byte[] bytes) {
        try {
            SSL_WrapperPacket wrapper = SSL_WrapperPacket.parseFrom(bytes);
            publish(AI_VISION_WRAPPER, wrapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        detectionReceiver.start();
    }
}
