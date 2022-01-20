package com.triton.ai.skills.basic_skills;

import proto.simulation.SslSimulationRobotControl;
import proto.triton.AiBasicSkills;

public class DribbleSkill {
    public static SslSimulationRobotControl.RobotCommand dribbleSkill(int id, AiBasicSkills.Dribble skill) {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(id);
        // TODO WORK ON DRIBBLE
        robotCommand.setDribblerSpeed(1);
        return robotCommand.build();
    }
}
