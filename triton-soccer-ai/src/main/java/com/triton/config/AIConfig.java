package com.triton.config;

import static com.triton.constant.RuntimeConstants.objectConfig;

public class AIConfig {
    public float nodeRadius;
    public float boundSafety;
    public float robotSafety;
    public double obstacleScale;
    public float gridExtend;

    public float kpPos;
    public float kiPos;
    public float kdPos;
    public float kpOrientation;
    public float kiOrientation;
    public float kdOrientation;

    public float getNodeSpacing() {
        return 2 * nodeRadius;
    }

    public float getNodeCollisionDist() {
        return 2 * nodeRadius;
    }

    public float getBoundCollisionDist() {
        return objectConfig.objectToCameraFactor * objectConfig.robotRadius
                + getNodeCollisionDist()
                + 2 * boundSafety;
    }

    public float getRobotCollisionDist() {
        return 2 * objectConfig.objectToCameraFactor * objectConfig.robotRadius
                + getNodeCollisionDist()
                + 2 * robotSafety;
    }
}
