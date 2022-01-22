package com.triton.ai.skills.basic_skills;

import com.triton.messaging.Exchange;
import com.triton.module.Module;
import proto.triton.AiBasicSkills;

import java.io.IOException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.simulation.SslSimulationRobotControl.*;

public class StopSkill {
    public static void stopSkill(Module module, int id, AiBasicSkills.Stop stop) throws IOException {
        RobotCommand.Builder robotCommand = RobotCommand.newBuilder();
        robotCommand.setId(id);
        RobotMoveCommand.Builder moveCommand = RobotMoveCommand.newBuilder();
        MoveGlobalVelocity.Builder globalVelocity = MoveGlobalVelocity.newBuilder();
        globalVelocity.setX(0);
        globalVelocity.setY(0);
        globalVelocity.setAngular(0);
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);
        module.publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }
}
