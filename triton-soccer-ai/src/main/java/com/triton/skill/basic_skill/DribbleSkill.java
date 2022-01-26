package com.triton.skill.basic_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import proto.simulation.SslSimulationRobotControl;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.triton.ObjectWithMetadata.Robot;

public class DribbleSkill extends Skill {
    private final Robot ally;
    private final boolean dribbleOn;

    public DribbleSkill(Module module, Robot ally, boolean dribbleOn) {
        super(module);
        this.ally = ally;
        this.dribbleOn = dribbleOn;
    }

    @Override
    public void run() {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(ally.getId());
        // TODO WORK ON DRIBBLE
        if (dribbleOn)
            robotCommand.setDribblerSpeed(1);
        else
            robotCommand.setDribblerSpeed(0);
        publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }
}
