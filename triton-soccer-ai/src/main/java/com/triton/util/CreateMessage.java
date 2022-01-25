package com.triton.util;

import com.triton.constant.Team;
import proto.simulation.SslGcCommon;
import proto.simulation.SslSimulationControl;

public class CreateMessage {
    public static SslSimulationControl.TeleportRobot createTeleportRobot(Team team, int id, float x, float y, float orientation) {
        SslSimulationControl.TeleportRobot.Builder teleportRobot = SslSimulationControl.TeleportRobot.newBuilder();
        SslGcCommon.RobotId.Builder robotId = SslGcCommon.RobotId.newBuilder();
        if (team == Team.YELLOW)
            robotId.setTeam(SslGcCommon.Team.YELLOW);
        else
            robotId.setTeam(SslGcCommon.Team.BLUE);
        robotId.setId(id);
        teleportRobot.setId(robotId);
        teleportRobot.setX(x / 1000f);
        teleportRobot.setY(y / 1000f);
        teleportRobot.setOrientation(orientation);
        teleportRobot.setPresent(true);
        teleportRobot.setByForce(false);
        return teleportRobot.build();
    }
}
