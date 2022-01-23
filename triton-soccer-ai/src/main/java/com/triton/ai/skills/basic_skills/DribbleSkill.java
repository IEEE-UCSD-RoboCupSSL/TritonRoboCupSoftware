package com.triton.ai.skills.basic_skills;

import com.triton.module.Module;
import proto.simulation.SslSimulationRobotControl;
import proto.triton.AiBasicSkills;

import java.io.IOException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;

public class DribbleSkill {
    public static void dribbleSkill(Module module, int id, AiBasicSkills.Dribble skill) throws IOException {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(id);
        // TODO WORK ON DRIBBLE
        robotCommand.setDribblerSpeed(0.5f);
        robotCommand.setKickSpeed(0);
        module.publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }
}
