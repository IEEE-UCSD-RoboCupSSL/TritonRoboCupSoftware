package com.triton.util;

import java.util.ArrayList;
import java.util.List;

import static com.triton.util.ProtobufUtils.*;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

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

    public static boolean isInAllyGoal(Ball ball, SSL_GeometryFieldSize field) {
        float goalX = -field.getGoalWidth() / 2f;
        float goalY = -field.getFieldLength() / 2f - field.getGoalDepth();
        float goalWidth = field.getGoalWidth();
        float goalHeight = field.getGoalDepth();
        return getPos(ball).isInRect(goalX, goalY, goalWidth, goalHeight);
    }

    public static boolean isInFoeGoal(Ball ball, SSL_GeometryFieldSize field) {
        float goalX = -field.getGoalWidth() / 2f;
        float goalY = field.getFieldLength() / 2f;
        float goalWidth = field.getGoalWidth();
        float goalHeight = field.getGoalDepth();
        return getPos(ball).isInRect(goalX, goalY, goalWidth, goalHeight);
    }

    public static boolean isInBounds(Ball ball, SSL_GeometryFieldSize field) {
        return isInBounds(getPos(ball), field);
    }

    public static boolean isInBounds(Vector2d pos, SSL_GeometryFieldSize field) {
        return pos.isInRect(-field.getFieldWidth() / 2f,
                -field.getFieldLength() / 2f,
                field.getFieldWidth(),
                field.getFieldLength());
    }

    public static boolean isInBounds(Robot robot, SSL_GeometryFieldSize field) {
        return isInBounds(getPos(robot), field);
    }

    public static float distToPath(Vector2d from, Vector2d to, List<Robot> robots) {
        List<Vector2d> points = new ArrayList<>();
        robots.forEach(robot -> points.add(getPos(robot)));
        return Vector2d.distToPath(from, to, points);
    }

    public static boolean checkDistToPath(Vector2d from, Vector2d to, List<Robot> robots, float dist) {
        List<Vector2d> points = new ArrayList<>();
        robots.forEach(robot -> points.add(getPos(robot)));
        return Vector2d.checkDistToPath(from, to, points, dist);
    }

    public static Robot getNearest(Vector2d target, List<Robot> robots) {
        Robot closestRobot = null;
        float minDist = Float.MAX_VALUE;
        for (Robot robot: robots) {
            float dist = target.dist(getPos(robot));
            if (dist < minDist) {
                closestRobot = robot;
                minDist = dist;
            }
        }
        return closestRobot;
    }
}
