package com.triton.routine.routines.leaf.action;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import com.triton.util.Vector2d;
import proto.simulation.SslSimulationRobotControl;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static com.triton.routine.base.StackId.TARGET_ANGULAR;
import static com.triton.routine.base.StackId.TARGET_VEL;

public class MatchVelocity extends Routine {
    public MatchVelocity() {
        super();
    }

    @Override
    public void reset() {
    }

    @Override
    public void act(Runner runner, Context context) {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(runner.getAllyId());
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveGlobalVelocity.Builder globalVelocity = SslSimulationRobotControl.MoveGlobalVelocity.newBuilder();

        Vector2d targetVel = (Vector2d) context.getVariable(runner.getAllyId(), TARGET_VEL);
        globalVelocity.setX(targetVel.x);
        globalVelocity.setY(targetVel.y);

        float targetAngular = (float) context.getVariable(runner.getAllyId(), TARGET_ANGULAR);
        globalVelocity.setAngular(targetAngular);

        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);

        runner.publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }
}
