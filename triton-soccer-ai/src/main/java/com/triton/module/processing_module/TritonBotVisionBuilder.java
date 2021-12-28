package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.module.Module;
import proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_TRITON_BOT_VISION;
import static com.triton.messaging.Exchange.AI_VISION_WRAPPER;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.TritonBotCommunication.TritonBotVision;

public class TritonBotVisionBuilder extends Module {
    public TritonBotVisionBuilder() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void loadConfig() throws IOException {
        super.loadConfig();
    }

    @Override
    protected void prepare() {
        super.prepare();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_VISION_WRAPPER, this::callbackWrapper);
        declarePublish(AI_TRITON_BOT_VISION);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        SSL_WrapperPacket wrapper;
        try {
            wrapper = (SSL_WrapperPacket) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        TritonBotVision.Builder tritonBotVision = TritonBotVision.newBuilder();
        // TODO

        try {
            publish(AI_TRITON_BOT_VISION, tritonBotVision);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
