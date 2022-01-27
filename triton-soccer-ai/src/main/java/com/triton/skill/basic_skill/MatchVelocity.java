package com.triton.skill.basic_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.util.Vector2d;
import proto.simulation.SslSimulationRobotControl;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.triton.ObjectWithMetadata.Robot;

public class MatchVelocity extends Skill {
    private final Robot actor;
    private final Vector2d vel;
    private final float angular;

    public MatchVelocity(Module module, Robot actor, Vector2d vel, float angular) {
        super(module);
        this.actor = actor;
        this.vel = vel;
        this.angular = angular;
    }

    @Override
    protected void execute() {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(actor.getId());
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveGlobalVelocity.Builder globalVelocity = SslSimulationRobotControl.MoveGlobalVelocity.newBuilder();
        globalVelocity.setX(vel.x);
        globalVelocity.setY(vel.y);
        globalVelocity.setAngular(angular);
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);
        publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }
}
