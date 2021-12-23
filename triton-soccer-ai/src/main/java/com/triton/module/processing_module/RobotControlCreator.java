package com.triton.module.processing_module;

import com.triton.module.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_ROBOT_CONTROL;
import static com.triton.messaging.Exchange.AI_TRITON_BOT_COMMAND;
import static proto.simulation.SslSimulationRobotControl.*;

public class RobotControlCreator extends Module {
    public RobotControlCreator() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(AI_ROBOT_CONTROL);
        declarePublish(AI_TRITON_BOT_COMMAND);
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                RobotControl.Builder robotControl = RobotControl.newBuilder();

                RobotCommand.Builder robotCommand = RobotCommand.newBuilder();

                robotCommand.setId(0);

                RobotMoveCommand.Builder moveCommand = RobotMoveCommand.newBuilder();
                MoveLocalVelocity.Builder localVelocity = MoveLocalVelocity.newBuilder();
                localVelocity.setForward(100);
                localVelocity.setLeft(0);
                localVelocity.setAngular(1);
                moveCommand.setLocalVelocity(localVelocity);
                robotCommand.setMoveCommand(moveCommand);

                robotCommand.setKickSpeed(10);
                robotCommand.setKickAngle(0);
                robotCommand.setDribblerSpeed(10);

                publish(AI_TRITON_BOT_COMMAND, robotCommand.build());

                robotControl.addRobotCommands(robotCommand);

                publish(AI_ROBOT_CONTROL, robotControl.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
