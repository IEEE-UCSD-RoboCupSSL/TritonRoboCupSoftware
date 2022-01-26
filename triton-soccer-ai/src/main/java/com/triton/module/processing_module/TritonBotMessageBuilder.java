package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.RobotCommand;
import static proto.triton.TritonBotCommunication.TritonBotMessage;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class TritonBotMessageBuilder extends Module {

    public TritonBotMessageBuilder(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    protected void prepare() {
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_TRITON_BOT_MESSAGE);
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_VISION_WRAPPER, this::callbackWrapper);
        declareConsume(AI_ROBOT_COMMAND, this::callbackRobotCommand);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        SSL_WrapperPacket wrapperPacket = (SSL_WrapperPacket) simpleDeserialize(delivery.getBody());
        SSL_DetectionFrame frame = wrapperPacket.getDetection();

        List<SSL_DetectionRobot> allies;
        if (RuntimeConstants.team == Team.BLUE)
            allies = frame.getRobotsBlueList();
        else
            allies = frame.getRobotsYellowList();

        allies.forEach(ally -> {
            TritonBotMessage.Builder message = TritonBotMessage.newBuilder();
            message.setId(ally.getRobotId());
            message.setVision(ally);
            publish(AI_TRITON_BOT_MESSAGE, message.build());
        });
    }

    private void callbackRobotCommand(String s, Delivery delivery) {
        RobotCommand robotCommand = (RobotCommand) simpleDeserialize(delivery.getBody());
        TritonBotMessage.Builder message = TritonBotMessage.newBuilder();
        message.setId(robotCommand.getId());
        message.setCommand(robotCommand);
        publish(AI_TRITON_BOT_MESSAGE, message.build());
    }
}
