package com.triton.skill.basic_skill;

import com.triton.helper.Vector2d;
import com.triton.module.Module;
import com.triton.skill.Skill;
import proto.simulation.SslSimulationRobotControl;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.triton.ObjectWithMetadata.Robot;

public class MatchVelocitySkill extends Skill {
    private Robot ally;
    private Vector2d vel;
    private float angular;

    public MatchVelocitySkill(Module module, Robot ally, Vector2d vel, float angular) {
        super(module);
        update(ally, vel, angular);
    }

    public void update(Robot ally, Vector2d vel, float angular) {
        this.ally = ally;
        this.vel = vel;
        this.angular = angular;
    }

    @Override
    public void run() {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(ally.getId());
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveGlobalVelocity.Builder globalVelocity = SslSimulationRobotControl.MoveGlobalVelocity.newBuilder();
        globalVelocity.setX(vel.x);
        globalVelocity.setY(vel.y);
        globalVelocity.setAngular(angular);
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);
        publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }
}
