package com.triton.config;

import static com.triton.constant.RuntimeConstants.objectConfig;

public class AIConfig {
    public float nodeRadius;
    public float boundSafety;
    public float robotSafety;
    public double obstacleScale;
    public float gridExtend;
    public float collisionSpeedScale;
    public float collisionExtrapolation;

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
