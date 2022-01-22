package com.triton.ai.skills.basic_skills;

import com.triton.module.Module;
import proto.triton.AiBasicSkills;

import java.io.IOException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.simulation.SslSimulationRobotControl.*;

public class KickSkill {
    public static void kickSkill(Module module, int id, AiBasicSkills.Kick skill) throws IOException {
        RobotCommand.Builder robotCommand = RobotCommand.newBuilder();
        robotCommand.setId(id);
        // TODO WORK ON CHIP
        RobotMoveCommand.Builder moveCommand = RobotMoveCommand.newBuilder();
        MoveLocalVelocity.Builder localCommand = MoveLocalVelocity.newBuilder();
        localCommand.setForward(0.1f);
        localCommand.setLeft(0);
        localCommand.setAngular(0);
        moveCommand.setLocalVelocity(localCommand);
        robotCommand.setMoveCommand(moveCommand);
        robotCommand.setKickSpeed(5f);
        robotCommand.setKickAngle(0);
        robotCommand.setDribblerSpeed(1);

        module.publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }
}
