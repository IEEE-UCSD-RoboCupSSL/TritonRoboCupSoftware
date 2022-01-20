package com.triton.ai.skills.basic_skills;

import proto.simulation.SslSimulationRobotControl;
import proto.triton.AiBasicSkills;
import proto.vision.MessagesRobocupSslDetection;

public class MoveToPointSkill {
    public static SslSimulationRobotControl.RobotCommand moveToPointSkill(int id, AiBasicSkills.MoveToPoint moveToPoint, MessagesRobocupSslDetection.SSL_DetectionRobot ally) {
        if (ally == null) return null;

        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(id);
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveGlobalVelocity.Builder globalVelocity = SslSimulationRobotControl.MoveGlobalVelocity.newBuilder();

        float vx = moveToPoint.getX() - ally.getX();
        float vy = moveToPoint.getY() - ally.getY();
        float angular = moveToPoint.getOrientation() - ally.getOrientation();

        // TODO CONSIDER SPEED CAP LATER
        vx /= 200;
        vy /= 200;
        angular /= 1;

        globalVelocity.setX(vx);
        globalVelocity.setY(vy);
        globalVelocity.setAngular(angular);
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);
        return robotCommand.build();
    }
}
