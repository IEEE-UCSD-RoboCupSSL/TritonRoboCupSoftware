package com.triton.config;

import static com.triton.constant.ProgramConstants.aiConfig;
import static com.triton.constant.ProgramConstants.objectConfig;

public class AIConfig {
    public float nodeRadius;
    public float boundSafety;
    public float robotSafety;
    public float penaltyScale;
    public float gridExtend;
    public float collisionSpeedScale;
    public float collisionExtrapolation;

    public float kickToPointAngleTolerance;

    public float kickFromPosDistTolerance;

    public float goalKickFromSearchDist;
    public float goalKickFromSearchSpacing;
    public float goalKickToSearchSpacing;
    public float goalDistToShooterScoreFactor;
    public float goalDistToObstaclesScoreFactor;

    public float passKickReceiverDistThreshold;
    public float passCatchBallSpeedThreshold;
    public float passCatchBallAngleTolerance;

    public float kpPos;
    public float kiPos;
    public float kdPos;
    public float kpOrientation;
    public float kiOrientation;
    public float kdOrientation;

    public float getNodeSpacing() {
        return 2 * nodeRadius;
    }

    public float calculateBoundPenalty(float dist) {
        return calculateGeneralPenalty(-dist, getBoundCollisionDist());
    }

    public float calculateGeneralPenalty(float dist, float collisionDistance) {
        return aiConfig.penaltyScale * (1 - (dist / collisionDistance));
    }

    public float getBoundCollisionDist() {
//        return objectConfig.objectToCameraFactor * objectConfig.robotRadius
//                + nodeRadius
//                + boundSafety;
        return nodeRadius + boundSafety;
    }

    public float calculateRobotPenalty(float dist) {
        return calculateGeneralPenalty(dist, getRobotCollisionDist());
    }

    public float getRobotCollisionDist() {
        return 2 * objectConfig.objectToCameraFactor * objectConfig.robotRadius
                + nodeRadius
                + robotSafety;
    }

    public float calculateRobotPenalty(float dist, float collisionExtension) {
        return calculateGeneralPenalty(dist, getRobotCollisionDist() + collisionExtension);
    }
}
