package com.triton.skill.individual_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import com.triton.skill.basic_skill.DribbleSkill;
import com.triton.skill.basic_skill.MoveToPointSkill;
import com.triton.util.Vector2d;

import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class GoalKeepSkill extends Skill {
    private final Robot ally;
    private final SSL_GeometryFieldSize field;
    private final Ball ball;

    public GoalKeepSkill(Module module, Robot ally, SSL_GeometryFieldSize field, Ball ball) {
        super(module);
        this.ally = ally;
        this.field = field;
        this.ball = ball;
    }

    @Override
    public void run() {
        float xMin = -1000f;
        float xMax = 1000f;
        float x = Math.min(Math.max(ball.getX(), xMin), xMax);
        float y = -field.getFieldLength() / 2f + 250f;
        Vector2d pos = new Vector2d(x, y);

        MoveToPointSkill moveToPointSkill = new MoveToPointSkill(module, ally, pos, (float) (Math.PI / 2));
        moveToPointSkill.start();

        DribbleSkill dribbleSkill = new DribbleSkill(module, ally, true);
        dribbleSkill.start();
    }
}
