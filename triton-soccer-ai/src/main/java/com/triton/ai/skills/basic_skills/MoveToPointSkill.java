package com.triton.ai.skills.basic_skills;

import com.triton.helper.Vector2d;
import com.triton.module.Module;
import proto.simulation.SslSimulationRobotControl;
import proto.triton.AiBasicSkills;

import java.io.IOException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.triton.ObjectWithMetadata.Robot;

public class MoveToPointSkill {
    public static void moveToPointSkill(Module module, int id, AiBasicSkills.MoveToPoint moveToPoint, Robot ally) throws IOException {
        if (ally == null) return;

        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(id);
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveGlobalVelocity.Builder globalVelocity = SslSimulationRobotControl.MoveGlobalVelocity.newBuilder();

        Vector2d vel = new Vector2d(moveToPoint.getX() - ally.getX(), moveToPoint.getY() - ally.getY());
        float angular = (moveToPoint.getOrientation() - ally.getOrientation());

        // TODO CONSIDER SPEED CAP LATER
        vel = vel.scale(1f / 200f);
        angular *= 1f / 2f;

        globalVelocity.setX(vel.x);
        globalVelocity.setY(vel.y);
        globalVelocity.setAngular(angular);
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);

        module.publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }
}
