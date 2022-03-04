package com.triton.routine.routines.leaf.action;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import proto.simulation.SslSimulationRobotControl;

import static com.triton.constant.ProgramConstants.objectConfig;
import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static com.triton.routine.base.StackId.KICK_SPEED;

public class Kick extends Routine {
    public Kick() {
        super();
    }

    @Override
    public void reset() {

    }

    @Override
    public void act(Runner runner, Context context) {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(runner.getAllyId());
        // TODO WORK ON CHIP
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveLocalVelocity.Builder localCommand = SslSimulationRobotControl.MoveLocalVelocity.newBuilder();
        localCommand.setForward(0.1f);
        localCommand.setLeft(0);
        localCommand.setAngular(0);
        moveCommand.setLocalVelocity(localCommand);
        robotCommand.setMoveCommand(moveCommand);
        float kickSpeed = (float) context.getVariable(runner.getAllyId(), KICK_SPEED);
        robotCommand.setKickSpeed(objectConfig.cameraToObjectFactor * kickSpeed);
        robotCommand.setKickAngle(0);
        robotCommand.setDribblerSpeed(0);
        runner.publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }
}
