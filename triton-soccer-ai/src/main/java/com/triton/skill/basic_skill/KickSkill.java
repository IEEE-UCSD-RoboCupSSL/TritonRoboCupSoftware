package com.triton.skill.basic_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import proto.simulation.SslSimulationRobotControl;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.triton.ObjectWithMetadata.Robot;

public class KickSkill extends Skill {
    private Robot ally;
    private boolean kickOn;
    private boolean chip;

    public KickSkill(Module module, Robot ally, boolean kickOn, boolean chip) {
        super(module);
        update(ally, kickOn, chip);
    }

    public void update(Robot ally, boolean kickOn, boolean chip) {
        this.ally = ally;
        this.kickOn = kickOn;
        this.chip = chip;
    }

    @Override
    public void run() {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(ally.getId());
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
}
