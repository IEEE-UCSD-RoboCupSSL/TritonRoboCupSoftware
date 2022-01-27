package com.triton.skill.basic_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import proto.simulation.SslSimulationRobotControl;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.triton.ObjectWithMetadata.Robot;

public class Dribble extends Skill {
    private final Robot actor;
    private final boolean dribbleOn;

    public Dribble(Module module, Robot actor, boolean dribbleOn) {
        super(module);
        this.actor = actor;
        this.dribbleOn = dribbleOn;
    }

    @Override
    protected void execute() {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(actor.getId());
        // TODO WORK ON DRIBBLE
        if (dribbleOn)
            robotCommand.setDribblerSpeed(1);
        else
            robotCommand.setDribblerSpeed(0);
        robotCommand.setKickSpeed(0);
        publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }
}
