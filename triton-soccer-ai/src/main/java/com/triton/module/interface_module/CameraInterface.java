package com.triton.module.interface_module;

import com.triton.constant.RuntimeConstants;
import com.triton.module.Module;
import com.triton.networking.UDP_MulticastReceiver;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_VISION_WRAPPER;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class CameraInterface extends Module {
    private UDP_MulticastReceiver detectionReceiver;

    public CameraInterface() {
        super();
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
        declarePublish(AI_VISION_WRAPPER);
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

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(detectionReceiver, 0, 10, TimeUnit.MILLISECONDS);
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
}
