package com.triton.ai.skills.basic_skills;

import com.triton.messaging.Exchange;
import com.triton.module.Module;
import proto.simulation.SslSimulationRobotControl;
import proto.triton.AiBasicSkills;

import java.io.IOException;

import static com.triton.messaging.Exchange.*;

public class MatchVelocitySkill {
    public static void matchVelocitySkill(Module module, int id, AiBasicSkills.MatchVelocity matchVelocity) throws IOException {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(id);
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveGlobalVelocity.Builder globalVelocity = SslSimulationRobotControl.MoveGlobalVelocity.newBuilder();
        globalVelocity.setX(matchVelocity.getVx());
        globalVelocity.setY(matchVelocity.getVy());
        globalVelocity.setAngular(matchVelocity.getAngular());
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);

        module.publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }
}
