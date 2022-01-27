package com.triton.config;

import static com.triton.constant.ProgramConstants.objectConfig;

public class AIConfig {
    public float nodeRadius;
    public float boundSafety;
    public float robotSafety;
    public double obstacleScale;
    public float gridExtend;
    public float collisionSpeedScale;
    public float collisionExtrapolation;

    public float kickToPointAngleTolerance;

    public float kickFromPosDistTolerance;

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

    public float getBoundCollisionDist() {
        return objectConfig.objectToCameraFactor * objectConfig.robotRadius
                + nodeRadius
                + boundSafety;
    }

    public float getRobotCollisionDist() {
        return 2 * objectConfig.objectToCameraFactor * objectConfig.robotRadius
                + nodeRadius
                + robotSafety;
    }
}
