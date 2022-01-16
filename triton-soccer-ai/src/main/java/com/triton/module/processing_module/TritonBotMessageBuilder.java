package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import proto.simulation.SslSimulationRobotControl;
import proto.vision.MessagesRobocupSslDetection;
import proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame;
import proto.vision.MessagesRobocupSslWrapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.*;
import static proto.triton.TritonBotCommunication.*;
import static proto.vision.MessagesRobocupSslDetection.*;
import static proto.vision.MessagesRobocupSslWrapper.*;

public class TritonBotMessageBuilder extends Module {
    private HashMap<Integer, Long> lastCommandTimeStamps;

    private static long commandDelay = 10;

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
        lastCommandTimeStamps = new HashMap<>();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_VISION_WRAPPER, this::callbackWrapper);
        declareConsume(AI_ROBOT_COMMAND, this::callbackRobotCommand);
        declarePublish(AI_TRITON_BOT_MESSAGE);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        SSL_WrapperPacket wrapper;
        try {
            wrapper = (SSL_WrapperPacket) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        List<SSL_DetectionRobot> allies;
        if (RuntimeConstants.team == Team.BLUE)
            allies = wrapper.getDetection().getRobotsBlueList();
        else
            allies = wrapper.getDetection().getRobotsYellowList();

        for (SSL_DetectionRobot ally : allies) {
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
        RobotCommand robotCommand;
        try {
            robotCommand = (RobotCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (lastCommandTimeStamps.containsKey(robotCommand.getId()) &&
                ((System.currentTimeMillis() - lastCommandTimeStamps.get(robotCommand.getId())) < commandDelay))
            return;

        TritonBotMessage.Builder message = TritonBotMessage.newBuilder();
        message.setId(robotCommand.getId());
        message.setCommand(robotCommand);

        try {
            publish(AI_TRITON_BOT_MESSAGE, message.build());
        } catch (IOException e) {
            e.printStackTrace();
        }

        lastCommandTimeStamps.put(robotCommand.getId(), System.currentTimeMillis());
    }
}
