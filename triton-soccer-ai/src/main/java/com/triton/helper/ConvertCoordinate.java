package com.triton.helper;

import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;

import java.util.ArrayList;
import java.util.List;

public class ConvertCoordinate {
    public static ArrayList<Float> audienceToBiased(ArrayList<Float> vector) {
        return audienceToBiased(vector.get(0), vector.get(1));
    }

    public static ArrayList<Float> audienceToBiased(float x, float y) {
        ArrayList<Float> biasedVector = new ArrayList<>();
        if (RuntimeConstants.team == Team.YELLOW) {
            biasedVector.add(-y);
            biasedVector.add(x);
        } else {
            biasedVector.add(y);
            biasedVector.add(-x);
        }
        return biasedVector;
    }

    public static float audienceToBiased(float angle) {
        if (RuntimeConstants.team == Team.YELLOW)
            return clampAngle((float) (angle + (Math.PI / 2)));
        else
            return clampAngle((float) (angle - (Math.PI / 2)));
    }

    public static List<Float> biasedToAudience(List<Float> vector) {
        return biasedToAudience(vector.get(0), vector.get(1));
    }

    public static List<Float> biasedToAudience(float x, float y) {
        ArrayList<Float> biasedVector = new ArrayList<>();
        if (RuntimeConstants.team == Team.YELLOW) {
            biasedVector.add(y);
            biasedVector.add(-x);
        } else {
            biasedVector.add(-y);
            biasedVector.add(x);
        }
        return biasedVector;
    }

    public static float biasedToAudience(float angle) {
        if (RuntimeConstants.team == Team.YELLOW)
            return clampAngle((float) (angle - (Math.PI / 2)));
        else
            return clampAngle((float) (angle + (Math.PI / 2)));
    }

    private static float clampAngle(float angle) {
        angle = (float) (angle % Math.PI * 2);
        angle = (float) ((angle + Math.PI * 2) % Math.PI * 2);

        if (angle > Math.PI)
            angle -= Math.PI * 2;
        return angle;
    }
}
