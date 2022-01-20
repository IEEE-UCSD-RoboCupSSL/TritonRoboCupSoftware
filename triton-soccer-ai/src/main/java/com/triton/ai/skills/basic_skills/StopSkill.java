package com.triton.ai.skills.basic_skills;

import proto.triton.AiBasicSkills;

import static proto.simulation.SslSimulationRobotControl.*;

public class StopSkill {
    public static RobotCommand stopSkill(int id, AiBasicSkills.Stop stop) {
        RobotCommand.Builder robotCommand = RobotCommand.newBuilder();
        robotCommand.setId(id);
        RobotMoveCommand.Builder moveCommand = RobotMoveCommand.newBuilder();
        MoveGlobalVelocity.Builder globalVelocity = MoveGlobalVelocity.newBuilder();
        globalVelocity.setX(0);
        globalVelocity.setY(0);
        globalVelocity.setAngular(0);
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);
        return robotCommand.build();
    }
}
