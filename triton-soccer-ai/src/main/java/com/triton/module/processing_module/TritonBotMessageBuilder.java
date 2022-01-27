package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslSimulationRobotControl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.RobotCommand;
import static proto.triton.TritonBotCommunication.TritonBotMessage;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class TritonBotMessageBuilder extends Module {
    private static final long PUBLISH_INTERVAL = 25;
    private static final long ROBOT_COMMAND_TIMEOUT = 250;
    private HashMap<Integer, TritonBotMessage.Builder> aggregateMessages;
    private HashMap<Integer, Long> robotCommandUpdateTimestamps;

    private Future publishFuture;

    public TritonBotMessageBuilder(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    public void run() {
        super.run();
        publishFuture = executor.scheduleAtFixedRate(this::publishMessages, 0, PUBLISH_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void publishMessages() {
        long timestamp = System.currentTimeMillis();
        aggregateMessages.forEach((id, message) -> {
            long timeDifference = timestamp - robotCommandUpdateTimestamps.get(id);
            if (timeDifference < ROBOT_COMMAND_TIMEOUT)
                publish(AI_TRITON_BOT_MESSAGE, message.build());
        });
    }

    @Override
    protected void prepare() {
        aggregateMessages = new HashMap<>();
        robotCommandUpdateTimestamps = new HashMap<>();

        for (int id = 0; id < RuntimeConstants.gameConfig.numBots; id++) {
            aggregateMessages.put(id, initDefaultMessage(id));
            robotCommandUpdateTimestamps.put(id, 0L);
        }
    }

    private TritonBotMessage.Builder initDefaultMessage(int id) {
        TritonBotMessage.Builder message = TritonBotMessage.newBuilder();
        message.setId(id);

        SSL_DetectionRobot.Builder ally = SSL_DetectionRobot.newBuilder();
        ally.setConfidence(0);
        ally.setRobotId(id);
        ally.setX(0);
        ally.setY(0);
        ally.setOrientation(0);
        ally.setPixelX(0);
        ally.setPixelY(0);
        ally.setHeight(0);
        message.setVision(ally.build());

        RobotCommand.Builder robotCommand = RobotCommand.newBuilder();
        robotCommand.setId(id);
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveGlobalVelocity.Builder globalVelocity = SslSimulationRobotControl.MoveGlobalVelocity.newBuilder();
        globalVelocity.setX(0);
        globalVelocity.setY(0);
        globalVelocity.setAngular(0);
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);
        robotCommand.setKickSpeed(0);
        robotCommand.setKickAngle(0);
        robotCommand.setDribblerSpeed(0);
        message.setCommand(robotCommand);
        return message;
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
            if (ally.getRobotId() >= RuntimeConstants.gameConfig.numBots) return;
            TritonBotMessage.Builder message = aggregateMessages.get(ally.getRobotId());
            message.setVision(ally);
        });
    }

    private void callbackRobotCommand(String s, Delivery delivery) {
        RobotCommand robotCommand = (RobotCommand) simpleDeserialize(delivery.getBody());

        TritonBotMessage.Builder aggregateRobotCommand = aggregateMessages.get(robotCommand.getId());
        RobotCommand.Builder command = aggregateRobotCommand.getCommandBuilder();
        if (robotCommand.hasMoveCommand())
            command.setMoveCommand(robotCommand.getMoveCommand());
        if (robotCommand.hasKickSpeed())
            command.setKickSpeed(robotCommand.getKickSpeed());
        if (robotCommand.hasKickAngle())
            command.setKickAngle(robotCommand.getKickAngle());
        if (robotCommand.hasDribblerSpeed())
            command.setDribblerSpeed(robotCommand.getDribblerSpeed());

        robotCommandUpdateTimestamps.put(robotCommand.getId(), System.currentTimeMillis());
    }

    @Override
    public void interrupt() {
        super.interrupt();
        publishFuture.cancel(false);
    }
}
