package com.triton.module.source_module;

import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl.TeleportRobot;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_SIMULATOR_COMMAND;
import static proto.simulation.SslGcCommon.RobotId;
import static proto.simulation.SslSimulationConfig.SimulatorConfig;
import static proto.simulation.SslSimulationControl.SimulatorCommand;
import static proto.simulation.SslSimulationControl.SimulatorControl;

public class SimulatorCommandSource extends Module {
    public SimulatorCommandSource() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(AI_BIASED_SIMULATOR_COMMAND);
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            SimulatorCommand.Builder command = SimulatorCommand.newBuilder();

            SimulatorControl.Builder control = SimulatorControl.newBuilder();
            TeleportRobot.Builder teleportBot = TeleportRobot.newBuilder();
            if (RuntimeConstants.team == Team.YELLOW) {
                teleportBot.setId(RobotId.newBuilder().setId(0).setTeam(SslGcCommon.Team.YELLOW));
            } else {
                teleportBot.setId(RobotId.newBuilder().setId(0).setTeam(SslGcCommon.Team.BLUE));
            }
            teleportBot.setX(0);
            teleportBot.setY(0);
            teleportBot.setOrientation((float) (Math.PI / 2));
            teleportBot.setVX(0);
            teleportBot.setVY(0);
            teleportBot.setVAngular(0);
            teleportBot.setPresent(true);
            teleportBot.setByForce(false);
            control.addTeleportRobot(teleportBot);
            command.setControl(control);

            SimulatorConfig.Builder config = SimulatorConfig.newBuilder();
            command.setConfig(config);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                publish(AI_BIASED_SIMULATOR_COMMAND, command.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
