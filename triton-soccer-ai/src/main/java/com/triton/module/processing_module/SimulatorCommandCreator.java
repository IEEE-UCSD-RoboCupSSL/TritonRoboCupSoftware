package com.triton.module.processing_module;

import com.triton.module.Module;
import proto.simulation.SslSimulationControl.TeleportRobot;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_SIMULATOR_COMMAND;
import static proto.simulation.SslGcCommon.RobotId;
import static proto.simulation.SslGcCommon.Team;
import static proto.simulation.SslSimulationConfig.SimulatorConfig;
import static proto.simulation.SslSimulationControl.SimulatorCommand;
import static proto.simulation.SslSimulationControl.SimulatorControl;

public class SimulatorCommandCreator extends Module {
    public SimulatorCommandCreator() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(AI_SIMULATOR_COMMAND);
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            SimulatorCommand.Builder command = SimulatorCommand.newBuilder();

            SimulatorControl.Builder control = SimulatorControl.newBuilder();
            TeleportRobot.Builder teleportBot = TeleportRobot.newBuilder();
            teleportBot.setId(RobotId.newBuilder().setId(0).setTeam(Team.BLUE));
            teleportBot.setX(0);
            teleportBot.setY(0);
            teleportBot.setOrientation(0);
            control.addTeleportRobot(teleportBot);
            command.setControl(control);

            SimulatorConfig.Builder config = SimulatorConfig.newBuilder();
            command.setConfig(config);

            try {
                publish(AI_SIMULATOR_COMMAND, command.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
