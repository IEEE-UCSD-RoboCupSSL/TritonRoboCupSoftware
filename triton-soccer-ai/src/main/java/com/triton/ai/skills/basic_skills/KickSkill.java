package com.triton.ai.skills.basic_skills;

import proto.simulation.SslSimulationRobotControl;
import proto.triton.AiBasicSkills;

public class KickSkill {
    public static SslSimulationRobotControl.RobotCommand kickSkill(int id, AiBasicSkills.Kick skill) {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(id);
        // TODO WORK ON CHIP
        robotCommand.setKickSpeed(1);
        robotCommand.setKickAngle((float) (Math.PI / 4));
        return robotCommand.build();
    }
}
