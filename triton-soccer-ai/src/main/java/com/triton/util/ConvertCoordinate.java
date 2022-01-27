package com.triton.util;

import com.triton.constant.ProgramConstants;
import com.triton.constant.Team;

import java.util.ArrayList;
import java.util.List;

public class ConvertCoordinate {
    public static Vector2d audienceToBiased(ArrayList<Float> vector) {
        return audienceToBiased(vector.get(0), vector.get(1));
    }

    public static Vector2d audienceToBiased(float x, float y) {
        Vector2d biasedVector;
        if (ProgramConstants.team == Team.YELLOW) {
            biasedVector = new Vector2d(-y, x);
        } else {
            biasedVector = new Vector2d(y, -x);
        }
        return biasedVector;
    }

    public static float audienceToBiased(float angle) {
        if (ProgramConstants.team == Team.YELLOW)
            return clampAngle((float) (angle + (Math.PI / 2)));
        else
            return clampAngle((float) (angle - (Math.PI / 2)));
    }

    private static float clampAngle(float angle) {
        while (angle < -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    public static Vector2d biasedToAudience(List<Float> vector) {
        return biasedToAudience(vector.get(0), vector.get(1));
    }

    public static Vector2d biasedToAudience(float x, float y) {
        Vector2d audVector;
        if (ProgramConstants.team == Team.YELLOW) {
            audVector = new Vector2d(y, -x);
        } else {
            audVector = new Vector2d(-y, x);
        }
        return audVector;
    }

    public static float biasedToAudience(float angle) {
        if (ProgramConstants.team == Team.YELLOW)
            return clampAngle((float) (angle - (Math.PI / 2)));
        else
            return clampAngle((float) (angle + (Math.PI / 2)));
    }
}
