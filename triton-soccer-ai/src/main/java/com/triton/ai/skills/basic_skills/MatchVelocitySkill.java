package com.triton.ai.skills.basic_skills;

import proto.simulation.SslSimulationRobotControl;
import proto.triton.AiBasicSkills;

public class MatchVelocitySkill {
    public static SslSimulationRobotControl.RobotCommand matchVelocitySkill(int id, AiBasicSkills.MatchVelocity matchVelocity) {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(id);
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveGlobalVelocity.Builder globalVelocity = SslSimulationRobotControl.MoveGlobalVelocity.newBuilder();
        globalVelocity.setX(matchVelocity.getVx());
        globalVelocity.setY(matchVelocity.getVy());
        globalVelocity.setAngular(matchVelocity.getAngular());
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);
        return robotCommand.build();
    }
}
