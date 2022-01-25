package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.RobotCommand;
import static proto.triton.TritonBotCommunication.TritonBotMessage;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class TritonBotMessageBuilder extends Module {
    private static final long VISION_INTERVAL = 0;
    private static final long COMMAND_INTERVAL = 0;

    private HashMap<Integer, Date> visionTimestamps;
    private HashMap<Integer, Date> commandTimestamps;
    private HashMap<Integer, RobotCommand.Builder> aggregateRobotCommands;

    public TritonBotMessageBuilder() {
        super();
    }

    @Override
    protected void prepare() {
        super.prepare();
        visionTimestamps = new HashMap<>();
        commandTimestamps = new HashMap<>();
        aggregateRobotCommands = new HashMap<>();
    }

    private void publishCommand() {
        aggregateRobotCommands.forEach((id, aggregateRobotCommand) -> {
            TritonBotMessage.Builder message = TritonBotMessage.newBuilder();
            message.setId(id);
            message.setCommand(aggregateRobotCommand);
            publish(AI_TRITON_BOT_MESSAGE, message.build());
        });
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
            Date timestamp = visionTimestamps.getOrDefault(ally.getRobotId(), new Date(0));
            Date currentTimestamp = new Date();
            long timestampDifference = currentTimestamp.getTime() - timestamp.getTime();
            if (timestampDifference > VISION_INTERVAL) {
                TritonBotMessage.Builder message = TritonBotMessage.newBuilder();
                message.setId(ally.getRobotId());
                message.setVision(ally);
                publish(AI_TRITON_BOT_MESSAGE, message.build());
                commandTimestamps.put(ally.getRobotId(), currentTimestamp);
            }
        });
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
        aggregateRobotCommands.put(robotCommand.getId(), aggregateRobotCommand);

        Date timestamp = commandTimestamps.getOrDefault(aggregateRobotCommand.getId(), new Date(0));
        Date currentTimestamp = new Date();
        long timestampDifference = currentTimestamp.getTime() - timestamp.getTime();
        if (timestampDifference > COMMAND_INTERVAL) {
            publishCommand();
            commandTimestamps.put(aggregateRobotCommand.getId(), currentTimestamp);
        }
    }
}
