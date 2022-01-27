package com.triton.skill.basic_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import proto.simulation.SslSimulationRobotControl;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.triton.ObjectWithMetadata.Robot;

public class Kick extends Skill {
    private final Robot actor;
    private final boolean kickOn;
    private final boolean chip;

    public Kick(Module module, Robot actor, boolean kickOn, boolean chip) {
        super(module);
        this.actor = actor;
        this.kickOn = kickOn;
        this.chip = chip;
    }

    @Override
    protected void execute() {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(actor.getId());
        // TODO WORK ON CHIP
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveLocalVelocity.Builder localCommand = SslSimulationRobotControl.MoveLocalVelocity.newBuilder();
        localCommand.setForward(0.1f);
        localCommand.setLeft(0);
        localCommand.setAngular(0);
        moveCommand.setLocalVelocity(localCommand);
        robotCommand.setMoveCommand(moveCommand);
        robotCommand.setKickSpeed(5f);
        robotCommand.setKickAngle(0);
        robotCommand.setDribblerSpeed(1);
        publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }
}
