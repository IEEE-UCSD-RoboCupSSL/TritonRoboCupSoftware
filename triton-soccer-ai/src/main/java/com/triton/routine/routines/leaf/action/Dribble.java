package com.triton.routine.routines.leaf.action;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import proto.simulation.SslSimulationRobotControl;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static com.triton.routine.base.StackId.DRIBBLE_ON;

public class Dribble extends Routine {
    public Dribble() {
        super();
    }

    @Override
    public void reset() {
    }

    @Override
    public void act(Runner runner, Context context) {

        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(runner.getAllyId());

        boolean dribbleOn = (boolean) context.getVariable(runner.getAllyId(), DRIBBLE_ON);
        if (dribbleOn)
            robotCommand.setDribblerSpeed(1);
        else
            robotCommand.setDribblerSpeed(0);
        robotCommand.setKickSpeed(0);
        runner.publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }
}
