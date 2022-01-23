package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.RobotCommand;
import static proto.triton.TritonBotCommunication.TritonBotMessage;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class TritonBotMessageBuilder extends Module {
    private static final long commandDelay = 0;
    private HashMap<Integer, Long> lastCommandTimeStamps;
    private HashMap<Integer, RobotCommand.Builder> aggregateRobotCommands;

    public TritonBotMessageBuilder() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void prepare() {
        super.prepare();
        lastCommandTimeStamps = new HashMap<>();
        aggregateRobotCommands = new HashMap<>();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_VISION_WRAPPER, this::callbackWrapper);
        declareConsume(AI_ROBOT_COMMAND, this::callbackRobotCommand);
        declarePublish(AI_TRITON_BOT_MESSAGE);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        SSL_WrapperPacket wrapper = (SSL_WrapperPacket) simpleDeserialize(delivery.getBody());

        List<SSL_DetectionRobot> allies;
        if (RuntimeConstants.team == Team.BLUE)
            allies = wrapper.getDetection().getRobotsBlueList();
        else
            allies = wrapper.getDetection().getRobotsYellowList();

        for (SSL_DetectionRobot ally : allies) {
            TritonBotMessage.Builder message = TritonBotMessage.newBuilder();
            message.setId(ally.getRobotId());
            message.setVision(ally);

            publish(AI_TRITON_BOT_MESSAGE, message.build());
        }
    }

    private void callbackRobotCommand(String s, Delivery delivery) {
        RobotCommand robotCommand = (RobotCommand) simpleDeserialize(delivery.getBody());

        if (!aggregateRobotCommands.containsKey(robotCommand.getId())) {
            RobotCommand.Builder aggregateRobotCommand = RobotCommand.newBuilder();
            aggregateRobotCommand.setId(robotCommand.getId());
            aggregateRobotCommands.put(robotCommand.getId(), aggregateRobotCommand);
        }

        RobotCommand.Builder aggregateRobotCommand = aggregateRobotCommands.get(robotCommand.getId());
        if (robotCommand.hasMoveCommand())
            aggregateRobotCommand.setMoveCommand(robotCommand.getMoveCommand());
        if (robotCommand.hasKickSpeed())
            aggregateRobotCommand.setKickSpeed(robotCommand.getKickSpeed());
        if (robotCommand.hasKickAngle())
            aggregateRobotCommand.setKickAngle(robotCommand.getKickAngle());
        if (robotCommand.hasDribblerSpeed())
            aggregateRobotCommand.setDribblerSpeed(robotCommand.getDribblerSpeed());

        if (lastCommandTimeStamps.containsKey(robotCommand.getId()) &&
                ((System.currentTimeMillis() - lastCommandTimeStamps.get(robotCommand.getId())) < commandDelay))
            return;

        TritonBotMessage.Builder message = TritonBotMessage.newBuilder();
        message.setId(robotCommand.getId());
        message.setCommand(aggregateRobotCommand);

        publish(AI_TRITON_BOT_MESSAGE, message.build());

        lastCommandTimeStamps.put(robotCommand.getId(), System.currentTimeMillis());
    }
}
