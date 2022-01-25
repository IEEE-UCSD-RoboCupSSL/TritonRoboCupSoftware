package com.triton.module.ai_module.skills.basic_skills;

import com.triton.helper.Vector2d;
import com.triton.module.SkillModule;
import proto.simulation.SslSimulationRobotControl;

import java.io.IOException;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;

public class MatchVelocitySkill extends SkillModule {
    private int allyId;
    private Vector2d vel;
    private float angular;

    public MatchVelocitySkill(int allyId, Vector2d vel, float angular) {
        super();
        this.allyId = allyId;
        this.vel = vel;
        this.angular = angular;
    }

    @Override
    protected void declareExchanges() throws IOException {
        super.declareExchanges();
        declarePublish(AI_BIASED_ROBOT_COMMAND);
    }

    @Override
    public void run() {
        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(allyId);
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveGlobalVelocity.Builder globalVelocity = SslSimulationRobotControl.MoveGlobalVelocity.newBuilder();
        globalVelocity.setX(vel.x);
        globalVelocity.setY(vel.y);
        globalVelocity.setAngular(angular);
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);
        publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());
    }

    public void setAllyId(int allyId) {
        this.allyId = allyId;
    }

    public void setVel(Vector2d vel) {
        this.vel = vel;
    }

    public void setAngular(float angular) {
        this.angular = angular;
    }
}
