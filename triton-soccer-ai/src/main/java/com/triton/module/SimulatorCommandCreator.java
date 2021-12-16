package com.triton.module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SIMULATOR_COMMAND;
import static proto.simulation.SslGcCommon.RobotId;
import static proto.simulation.SslGcCommon.Team.YELLOW;
import static proto.simulation.SslSimulationConfig.SimulatorConfig;
import static proto.simulation.SslSimulationControl.*;

public class SimulatorCommandCreator extends Module {
    public SimulatorCommandCreator() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(SIMULATOR_COMMAND);
    }

    @Override
    public void run() {
        super.run();

        SimulatorCommand.Builder command = SimulatorCommand.newBuilder();

        SimulatorControl.Builder control = SimulatorControl.newBuilder();

        TeleportRobot.Builder teleBot = TeleportRobot.newBuilder();
        RobotId.Builder botID = RobotId.newBuilder();
        botID.setId(0);
        botID.setTeam(YELLOW);
        teleBot.setId(botID.build());
        teleBot.setX(0);
        teleBot.setY(0);
        teleBot.setPresent(true);
        control.addTeleportRobot(teleBot.build());

        command.setControl(control.build());

        SimulatorConfig.Builder config = SimulatorConfig.newBuilder();
        command.setConfig(config.build());

        try {
            publish(SIMULATOR_COMMAND, command.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
