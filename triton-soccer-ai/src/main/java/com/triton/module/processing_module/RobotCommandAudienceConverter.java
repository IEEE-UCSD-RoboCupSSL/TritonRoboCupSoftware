package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.helper.ConvertCoordinate;
import com.triton.module.Module;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_ROBOT_COMMAND;
import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationRobotControl.*;

public class RobotCommandAudienceConverter extends Module {

    public RobotCommandAudienceConverter() throws IOException, TimeoutException {
        super();
    }

    private static RobotCommand biasedToAudience(RobotCommand command) {
        RobotCommand.Builder audienceCommand = command.toBuilder();
        audienceCommand.setMoveCommand(biasedToAudience(command.getMoveCommand()));
        return audienceCommand.build();
    }

    private static RobotMoveCommand biasedToAudience(RobotMoveCommand moveCommand) {
        if (moveCommand.hasGlobalVelocity()) {
            RobotMoveCommand.Builder audienceMoveCommand = moveCommand.toBuilder();
            audienceMoveCommand.setGlobalVelocity(biasedToAudience(audienceMoveCommand.getGlobalVelocity()));
            return audienceMoveCommand.build();
        }

        return moveCommand;
    }

    private static MoveGlobalVelocity biasedToAudience(MoveGlobalVelocity globalVelocity) {
        MoveGlobalVelocity.Builder audienceGlobalVelocity = globalVelocity.toBuilder();

        List<Float> audiencePosition = ConvertCoordinate.biasedToAudience(globalVelocity.getX(), globalVelocity.getY());
        audienceGlobalVelocity.setX(audiencePosition.get(0));
        audienceGlobalVelocity.setY(audiencePosition.get(1));

        audienceGlobalVelocity.setAngular(ConvertCoordinate.audienceToBiased(globalVelocity.getAngular()));

        return audienceGlobalVelocity.build();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_ROBOT_COMMAND, this::callbackBiasedRobotCommand);
        declarePublish(AI_ROBOT_COMMAND);
    }

    private void callbackBiasedRobotCommand(String s, Delivery delivery) {
        RobotCommand biasedRobotCommand;
        try {
            biasedRobotCommand = (RobotCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        RobotCommand robotCommand = biasedToAudience(biasedRobotCommand);
        try {
            publish(AI_ROBOT_COMMAND, robotCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
