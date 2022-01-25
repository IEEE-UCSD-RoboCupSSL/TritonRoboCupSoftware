package com.triton.module.ai_module.skills.basic_skills;

import com.triton.module.SkillModule;
import proto.simulation.SslSimulationRobotControl;

import java.io.IOException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;

public class KickSkill extends SkillModule {
    private int allyId;
    private boolean kickOn;
    private boolean chip;

    public KickSkill(int allyId, boolean kickOn, boolean chip) {
        super();
        this.allyId = allyId;
        this.kickOn = kickOn;
        this.chip = chip;
    }

    @Override
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }

    @Override
    public void run() {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(allyId);
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

    public void setAllyId(int allyId) {
        this.allyId = allyId;
    }

    public void setKickOn(boolean kickOn) {
        this.kickOn = kickOn;
    }

    public void setChip(boolean chip) {
        this.chip = chip;
    }
}
