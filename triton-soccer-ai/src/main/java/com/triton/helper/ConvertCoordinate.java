package com.triton.helper;

import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;

import java.util.ArrayList;
import java.util.List;

public class ConvertCoordinate {
    public static List<Float> audienceToBiased(List<Float> vector) {
        return audienceToBiased(vector.get(0), vector.get(1));
    }

    public static List<Float> audienceToBiased(float x, float y) {
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

    public static float audienceToBiased(float orientation) {
        // TODO Bound to -180 and 180
        if (RuntimeConstants.team == Team.YELLOW) {
            return (float) ((orientation + (Math.PI / 2)) % (2 * Math.PI));
        } else {
            return (float) ((orientation - (Math.PI / 2)) % (2 * Math.PI));
        }
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

    public static float biasedToAudience(float orientation) {
        // TODO Bound to -180 and 180
        if (RuntimeConstants.team == Team.YELLOW) {
            return (float) ((orientation - (Math.PI / 2)) % (2 * Math.PI));
        } else {
            return (float) ((orientation + (Math.PI / 2)) % (2 * Math.PI));
        }
    }
}
