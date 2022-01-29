package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.Dribble;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.util.ObjectHelper.getNearest;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.FilteredObject.*;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class GoalKeep extends Skill {
    private final Robot actor;
    private final FilteredWrapperPacket wrapper;

    public GoalKeep(Module module, Robot actor, FilteredWrapperPacket wrapper) {
        super(module);
        this.actor = actor;
        this.wrapper = wrapper;
    }

    @Override
    protected void execute() {
        SSL_GeometryFieldSize field = wrapper.getField();
        Ball ball = wrapper.getBall();
        Map<Integer, Robot> allies = wrapper.getAlliesMap();
        Map<Integer, Robot> foes = wrapper.getFoesMap();

        float xMin = -field.getGoalWidth() / 2f;
        float xMax = field.getGoalWidth() / 2f;
        float y = -field.getFieldLength() / 2f + 250f;

        Vector2d pos;
        Vector2d ballPos;

        if (ball.getConfidence() == 1f) {
            float x = Math.min(Math.max(ball.getX(), xMin), xMax);
            pos = new Vector2d(x, y);
            ballPos = getPos(ball);
        } else {
            Robot foe;
            if (ball.hasFoeCapture()) {
                int foeId = ball.getFoeCapture().getId();
                foe = foes.get(foeId);
            } else {
                float goalX = 0;
                float goalY = -field.getFieldLength() / 2f;
                Vector2d goalPos = new Vector2d(goalX, goalY);
                foe = getNearest(goalPos, foes.values().stream().toList());
            }

            Vector2d nearestFoeFaceDir = new Vector2d(foe.getOrientation());
            Vector2d predictNearestFoe = getPos(foe).add(nearestFoeFaceDir.scale(1000));

            float x = Math.min(Math.max(predictNearestFoe.x, xMin), xMax);
            pos = new Vector2d(x, y);
            ballPos = getPos(foe);
        }

        MoveToTarget moveToTarget = new MoveToTarget(module, actor, pos, ballPos);
        submitSkill(moveToTarget);

        Dribble dribble = new Dribble(module, actor, true);
        submitSkill(dribble);
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
    }
}
