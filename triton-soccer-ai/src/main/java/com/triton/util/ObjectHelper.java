package com.triton.util;

import static com.triton.util.ProtobufUtils.*;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class ObjectHelper {
    public static boolean hasOrientation(Robot robot, Vector2d facePos, float angleTolerance) {
        float targetOrientation = (float) Math.atan2(facePos.y - robot.getY(), facePos.x - robot.getX());
        return hasOrientation(robot, targetOrientation, angleTolerance);
    }

    public static boolean hasOrientation(Robot robot, float orientation, float angleTolerance) {
        float angleDifference = Vector2d.angleDifference(orientation, robot.getOrientation());
        return Math.abs(angleDifference) < angleTolerance;
    }

    public static boolean hasPos(Robot robot, Vector2d pos, float distanceTolerance) {
        Vector2d robotPos = new Vector2d(robot.getX(), robot.getY());
        return robotPos.dist(pos) < distanceTolerance;
    }

    public static boolean willArriveAtTarget(Ball ball, Vector2d pos, float delta, float distanceTolerance) {
        return willArriveAtTarget(getPos(ball), getVel(ball), getAcc(ball), pos, delta, distanceTolerance);
    }

    public static boolean willArriveAtTarget(Vector2d pos, Vector2d vel, Vector2d acc, Vector2d target, float delta,
                                             float distanceTolerance) {
        Vector2d predictPos = predictPos(pos, vel, acc, delta);
        return predictPos.dist(target) < distanceTolerance;
    }

    public static Vector2d predictPos(Vector2d pos, Vector2d vel, Vector2d acc, float delta) {
        // TODO ADD ACC PREDICTION
        return pos.add(vel.scale(delta));
    }

    public static boolean willArriveAtTarget(Robot robot, Vector2d target, float delta, float distanceTolerance) {
        return willArriveAtTarget(getPos(robot), getVel(robot), getAcc(robot), target, delta, distanceTolerance);
    }

    public static boolean isMovingTowardTarget(Ball ball, Vector2d pos, float angleTolerance) {
        return isMovingTowardTarget(getPos(ball), getVel(ball), pos, angleTolerance);
    }

    public static boolean isMovingTowardTarget(Vector2d pos, Vector2d vel, Vector2d target, float angleTolerance) {
        Vector2d towardTarget = target.sub(pos);
        return vel.angle(towardTarget) < angleTolerance;
    }

    public static boolean isMovingTowardTarget(Ball ball, Vector2d target, float speedThreshold, float angleTolerance) {
        return isMovingTowardTarget(getPos(ball), getVel(ball), target, speedThreshold, angleTolerance);
    }

    public static boolean isMovingTowardTarget(Vector2d pos, Vector2d vel, Vector2d target,
                                               float speedThreshold, float angleTolerance) {
        Vector2d towardTarget = target.sub(pos);
        return vel.mag() > speedThreshold && vel.angle(towardTarget) < angleTolerance;
    }

    public static boolean isMovingTowardTarget(Robot robot, Vector2d pos, float angleTolerance) {
        return isMovingTowardTarget(getPos(robot), getVel(robot), pos, angleTolerance);
    }

    public static boolean isMovingTowardTarget(Robot robot, Vector2d pos, float speedThreshold, float angleTolerance) {
        return isMovingTowardTarget(getPos(robot), getVel(robot), pos, speedThreshold, angleTolerance);
    }

    public static Vector2d predictPos(Robot robot, float delta) {
        return predictPos(getPos(robot), getVel(robot), getAcc(robot), delta);
    }

    public static Vector2d predictPos(Ball ball, float delta) {
        return predictPos(getPos(ball), getVel(ball), getAcc(ball), delta);
    }

    public static float predictOrientation(Robot robot, float delta) {
        return predictOrientation(robot.getOrientation(), robot.getAngular(), robot.getAccAngular(), delta);
    }

    public static float predictOrientation(float orientation, float angular, float accAngular, float delta) {
        // TODO ADD ACC PREDICTION
        return orientation
                + delta * angular;
    }
}
