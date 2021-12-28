package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslSimulationRobotControl;
import proto.vision.MessagesRobocupSslDetection;
import proto.vision.MessagesRobocupSslWrapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.TritonBotCommunication.*;

public class TritonBotMessageBuilder extends Module {
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
        declareConsume(AI_VISION_WRAPPER, this::callbackWrapper);
        declareConsume(AI_ROBOT_COMMAND, this::callbackRobotCommand);
        declarePublish(AI_TRITON_BOT_MESSAGE);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        MessagesRobocupSslWrapper.SSL_WrapperPacket wrapper;
        try {
            wrapper = (MessagesRobocupSslWrapper.SSL_WrapperPacket) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        List<MessagesRobocupSslDetection.SSL_DetectionRobot> allies;
        if (RuntimeConstants.team == Team.BLUE)
            allies = wrapper.getDetection().getRobotsBlueList();
        else
            allies = wrapper.getDetection().getRobotsYellowList();

        for (MessagesRobocupSslDetection.SSL_DetectionRobot ally : allies) {
            TritonBotMessage.Builder message = TritonBotMessage.newBuilder();
            message.setId(ally.getRobotId());
            message.setVision(ally);

            try {
                publish(AI_TRITON_BOT_MESSAGE, message.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void callbackRobotCommand(String s, Delivery delivery) {
        SslSimulationRobotControl.RobotCommand robotCommand;
        try {
            robotCommand = (SslSimulationRobotControl.RobotCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        TritonBotMessage.Builder message = TritonBotMessage.newBuilder();
        message.setId(robotCommand.getId());
        message.setCommand(robotCommand);

        try {
            publish(AI_TRITON_BOT_MESSAGE, message.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
