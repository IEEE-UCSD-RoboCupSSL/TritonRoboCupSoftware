package com.triton.module.source_module;

import com.triton.module.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.simulation.SslSimulationRobotControl.*;

public class RobotCommandSource extends Module {
    public RobotCommandSource() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                RobotCommand.Builder robotCommand = RobotCommand.newBuilder();

                robotCommand.setId(0);

                RobotMoveCommand.Builder moveCommand = RobotMoveCommand.newBuilder();

                MoveGlobalVelocity.Builder globalVelocity = MoveGlobalVelocity.newBuilder();
                globalVelocity.setX(0);
                globalVelocity.setY(3);
                globalVelocity.setAngular((float) (3 * 2 * Math.PI));
                moveCommand.setGlobalVelocity(globalVelocity);
                robotCommand.setMoveCommand(moveCommand);

//                MoveLocalVelocity.Builder localVelocity = MoveLocalVelocity.newBuilder();
//                localVelocity.setLeft(1);
//                localVelocity.setForward(1);
//                localVelocity.setAngular(1);
//                moveCommand.setLocalVelocity(localVelocity);
//
//                MoveWheelVelocity.Builder wheelVelocity = MoveWheelVelocity.newBuilder();
//                wheelVelocity.setFrontRight(1);
//                wheelVelocity.setBackRight(1);
//                wheelVelocity.setBackLeft(1);
//                wheelVelocity.setFrontLeft(1);
//                moveCommand.setWheelVelocity(wheelVelocity);
//
//                robotCommand.setKickSpeed(10);
//                robotCommand.setKickAngle(0);
//                robotCommand.setDribblerSpeed(10);

                publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
