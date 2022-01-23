package com.triton.ai.skills.basic_skills;

import com.triton.helper.Vector2d;
import com.triton.module.Module;
import proto.simulation.SslSimulationRobotControl;
import proto.triton.AiBasicSkills;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.triton.messaging.Exchange.AI_BIASED_ROBOT_COMMAND;
import static proto.triton.ObjectWithMetadata.Robot;

public class MoveToPointSkill {
    private static final float kpPos = 0.005f;
    private static final float kiPos = 0.0000001f;
    private static final float kdPos = 0.001f;

    private static final float kpOrientation = 0.5f;
    private static final float kiOrientation = 0.0000001f;
    private static final float kdOrientation = 0.001f;

    private static Map<Integer, Long> lastTimestampMap;
    private static Map<Integer, Vector2d> errorSumPosMap;
    private static Map<Integer, Float> errorSumOrientationMap;
    private static Map<Integer, Vector2d> lastErrorPosMap;
    private static Map<Integer, Float> lastErrorOrientationMap;

    public static void moveToPointSkill(Module module, int id, AiBasicSkills.MoveToPoint moveToPoint, Robot ally) throws IOException {
        init(id);

        if (ally == null) return;

        long timeDiff = System.currentTimeMillis() - lastTimestampMap.get(id);
        if (timeDiff == 0) return;

        Vector2d inputPos = new Vector2d(ally.getX(), ally.getY());
        Vector2d targetPos = new Vector2d(moveToPoint.getX(), moveToPoint.getY());
        Vector2d outputVel = pidPos(id, inputPos, targetPos, timeDiff);

        float inputOrientation = ally.getOrientation();
        float targetOrientation = moveToPoint.getOrientation();
        float outputAngular = pidOrientation(id, inputOrientation, targetOrientation, timeDiff);

        SslSimulationRobotControl.RobotCommand.Builder robotCommand = SslSimulationRobotControl.RobotCommand.newBuilder();
        robotCommand.setId(id);
        SslSimulationRobotControl.RobotMoveCommand.Builder moveCommand = SslSimulationRobotControl.RobotMoveCommand.newBuilder();
        SslSimulationRobotControl.MoveGlobalVelocity.Builder globalVelocity = SslSimulationRobotControl.MoveGlobalVelocity.newBuilder();
        globalVelocity.setX(outputVel.x);
        globalVelocity.setY(outputVel.y);
        globalVelocity.setAngular(outputAngular);
        moveCommand.setGlobalVelocity(globalVelocity);
        robotCommand.setMoveCommand(moveCommand);
        module.publish(AI_BIASED_ROBOT_COMMAND, robotCommand.build());

        lastTimestampMap.put(id, System.currentTimeMillis());
    }

    private static void init(int id) {
        if (lastTimestampMap == null)
            lastTimestampMap = new HashMap<>();

        if (errorSumPosMap == null)
            errorSumPosMap = new HashMap<>();
        if (errorSumOrientationMap == null)
            errorSumOrientationMap = new HashMap<>();

        if (lastErrorPosMap == null)
            lastErrorPosMap = new HashMap<>();
        if (lastErrorOrientationMap == null)
            lastErrorOrientationMap = new HashMap<>();

        if (!lastTimestampMap.containsKey(id))
            lastTimestampMap.put(id, System.currentTimeMillis());

        if (!errorSumPosMap.containsKey(id))
            errorSumPosMap.put(id, new Vector2d(0, 0));
        if (!errorSumOrientationMap.containsKey(id))
            errorSumOrientationMap.put(id, 0f);

        if (!lastErrorPosMap.containsKey(id))
            lastErrorPosMap.put(id, new Vector2d(0, 0));
        if (!lastErrorOrientationMap.containsKey(id))
            lastErrorOrientationMap.put(id, 0f);
    }

    private static Vector2d pidPos(int id, Vector2d input, Vector2d target, long timeDiff) {
        if (Float.isNaN(input.x) || Float.isNaN(input.y) || Float.isNaN(target.x) || Float.isNaN(target.y))
            return new Vector2d(0, 0);

        Vector2d errorSum = errorSumPosMap.get(id);
        Vector2d lastError = lastErrorPosMap.get(id);

        Vector2d error = target.sub(input);
        Vector2d updatedErrorSum = errorSum.add(error.scale(timeDiff));
        Vector2d errorDiff = error.sub(lastError).scale(1f / timeDiff);

        errorSumPosMap.put(id, updatedErrorSum);
        lastErrorPosMap.put(id, error);

        return error.scale(kpPos)
                .add(errorSum.scale(kiPos))
                .add(errorDiff.scale(kdPos));
    }

    private static float pidOrientation(int id, float inputOrientation, float targetOrientation, long timeDiff) {
        if (Float.isNaN(inputOrientation) || Float.isNaN(targetOrientation))
            return 0;

        float errorSum = errorSumOrientationMap.get(id);
        float lastError = lastErrorOrientationMap.get(id);

        float error = targetOrientation - inputOrientation;
        float updatedErrorSum = errorSum + error * timeDiff;
        float errorDiff = (error - lastError) / timeDiff;

        errorSumOrientationMap.put(id, updatedErrorSum);
        lastErrorOrientationMap.put(id, error);

        return error * kpOrientation
                + updatedErrorSum * kiOrientation
                + errorDiff * kdOrientation;
    }
}
