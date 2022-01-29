package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.Dribble;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.triton.util.ObjectHelper.getNearest;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class GoalKeep extends Skill {
    private final Robot actor;
    private final SSL_GeometryFieldSize field;
    private final Ball ball;
    private final Map<Integer, Robot> foes;

    public GoalKeep(Module module, Robot actor, SSL_GeometryFieldSize field, Ball ball, Map<Integer, Robot> foes) {
        super(module);
        this.actor = actor;
        this.field = field;
        this.ball = ball;
        this.foes = foes;
    }

    @Override
    protected void execute() {
        float xMin = -field.getGoalWidth() / 2f;
        float xMax = field.getGoalWidth() / 2f;
        float y = -field.getFieldLength() / 2f + 250f;

        Vector2d pos = null;
        Vector2d ballPos = null;

        if (ball.getConfidence() == 1f) {
            float x = Math.min(Math.max(ball.getX(), xMin), xMax);
            pos = new Vector2d(x, y);
            ballPos = getPos(ball);
        } else {
            float goalX = 0;
            float goalY = -field.getFieldLength() / 2f;
            Vector2d goalPos = new Vector2d(goalX, goalY);

            Robot nearestFoe = getNearest(goalPos, new ArrayList<>(foes.values()));
            Vector2d nearestFoeFaceDir = new Vector2d(nearestFoe.getOrientation());
            Vector2d predictNearestFoe = getPos(nearestFoe).add(nearestFoeFaceDir.scale(1000));

            float x = Math.min(Math.max(predictNearestFoe.x, xMin), xMax);
            pos = new Vector2d(x, y);
            ballPos = getPos(nearestFoe);
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
