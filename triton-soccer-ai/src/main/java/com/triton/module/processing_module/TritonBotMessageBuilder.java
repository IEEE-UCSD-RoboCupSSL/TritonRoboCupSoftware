package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.module.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.TritonBotCommunication.*;

public class TritonBotMessageBuilder extends Module {
    private TritonBotVision tritonBotVision;
    private TritonBotCommand tritonBotCommand;

    public TritonBotMessageBuilder() throws IOException, TimeoutException {
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
        declareConsume(AI_TRITON_BOT_VISION, this::callbackTritonBotVision);
        declareConsume(AI_TRITON_BOT_COMMAND, this::callbackTritonBotCommand);
        declarePublish(AI_TRITON_BOT_MESSAGE);
    }

    private void callbackTritonBotVision(String s, Delivery delivery) {
        TritonBotVision tritonBotVision;
        try {
            tritonBotVision = (TritonBotVision) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        this.tritonBotVision = tritonBotVision;
        publishTritonBotMessage();
    }

    private void callbackTritonBotCommand(String s, Delivery delivery) {
        TritonBotCommand tritonBotCommand;
        try {
            tritonBotCommand = (TritonBotCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        this.tritonBotCommand = tritonBotCommand;
        publishTritonBotMessage();
    }

    private void publishTritonBotMessage() {
        TritonBotMessage.Builder tritonBotMessage = TritonBotMessage.newBuilder();
        tritonBotMessage.setTritonBotVision(tritonBotVision);
        tritonBotMessage.setTritonBotCommand(tritonBotCommand);

        try {
            publish(AI_TRITON_BOT_MESSAGE, tritonBotMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
