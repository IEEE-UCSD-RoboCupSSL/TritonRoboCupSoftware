package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.helper.ConvertCoordinate;
import com.triton.module.Module;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_SIMULATOR_COMMAND;
import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_COMMAND;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.simulation.SslSimulationControl.*;

public class SimulatorCommandAudienceConverter extends Module {
    public SimulatorCommandAudienceConverter() throws IOException, TimeoutException {
        super();
    }

    private static SimulatorCommand audienceToBiased(SimulatorCommand command) {
        SimulatorCommand.Builder biasedCommand = command.toBuilder();
        biasedCommand.setControl(biasedToAudience(command.getControl()));
        return biasedCommand.build();
    }

    private static SimulatorControl biasedToAudience(SimulatorControl control) {
        SimulatorControl.Builder audienceControl = control.toBuilder();
        audienceControl.setTeleportBall(biasedToAudience(control.getTeleportBall()));

        audienceControl.clearTeleportRobot();
        for (TeleportRobot telportRobot : control.getTeleportRobotList()) {
            audienceControl.addTeleportRobot(biasedToAudience(telportRobot));
        }

        return audienceControl.build();
    }

    private static TeleportBall biasedToAudience(TeleportBall teleportBall) {
        TeleportBall.Builder audienceTeleportBall = teleportBall.toBuilder();

        List<Float> audiencePosition = ConvertCoordinate.biasedToAudience(teleportBall.getX(), teleportBall.getY());
        audienceTeleportBall.setX(audiencePosition.get(0));
        audienceTeleportBall.setY(audiencePosition.get(1));

        List<Float> audienceVelocity = ConvertCoordinate.biasedToAudience(teleportBall.getVx(), teleportBall.getVy());
        audienceTeleportBall.setVx(audienceVelocity.get(0));
        audienceTeleportBall.setVy(audienceVelocity.get(1));

        return audienceTeleportBall.build();
    }

    private static TeleportRobot biasedToAudience(TeleportRobot teleportRobot) {
        TeleportRobot.Builder audienceTeleportRobot = teleportRobot.toBuilder();

        List<Float> audiencePosition = ConvertCoordinate.biasedToAudience(teleportRobot.getX(), teleportRobot.getY());
        audienceTeleportRobot.setX(audiencePosition.get(0));
        audienceTeleportRobot.setY(audiencePosition.get(1));

        List<Float> audienceVelocity = ConvertCoordinate.biasedToAudience(teleportRobot.getVX(), teleportRobot.getVY());
        audienceTeleportRobot.setVX(audienceVelocity.get(0));
        audienceTeleportRobot.setVY(audienceVelocity.get(1));

        audienceTeleportRobot.setOrientation(ConvertCoordinate.biasedToAudience(teleportRobot.getOrientation()));
        audienceTeleportRobot.setVAngular(ConvertCoordinate.biasedToAudience(teleportRobot.getVAngular()));

        return audienceTeleportRobot.build();
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
        declareConsume(AI_BIASED_SIMULATOR_COMMAND, this::callbackBiasedSimulatorCommand);
        declarePublish(AI_SIMULATOR_COMMAND);
    }

    private void callbackBiasedSimulatorCommand(String s, Delivery delivery) {
        SimulatorCommand biasedSimulatorCommand;
        try {
            biasedSimulatorCommand = (SimulatorCommand) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        SimulatorCommand simulatorCommand = audienceToBiased(biasedSimulatorCommand);
        try {
            publish(AI_SIMULATOR_COMMAND, simulatorCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
