package com.triton.ai.skills.individual_skills;

import com.triton.messaging.Exchange;
import com.triton.module.Module;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.HashMap;

import static proto.triton.AiBasicSkills.*;
import static proto.triton.AiIndividualSkills.GoalKeep;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class GoalKeepSkill {
    public static void goalKeepSkill(Module module, int id, GoalKeep goalKeep, MessagesRobocupSslGeometry.SSL_GeometryFieldSize field, HashMap<Integer, Robot> allies, Ball ball) throws IOException {
        BasicSkill.Builder moveToPointSkill = BasicSkill.newBuilder();
        MoveToPoint.Builder moveToPoint = MoveToPoint.newBuilder();

        float xMin = -1000f;
        float xMax = 1000f;
        float x = Math.min(Math.max(ball.getX(), xMin), xMax);
        float y = -field.getFieldLength() / 2f + 250f;

        moveToPoint.setOrientation((float) (Math.PI / 2));
        moveToPoint.setX(x);
        moveToPoint.setY(y);
        moveToPointSkill.setMoveToPoint(moveToPoint);

        module.publish(Exchange.AI_BASIC_SKILL, moveToPointSkill.build());

        BasicSkill.Builder dribbleSkill = BasicSkill.newBuilder();
        Dribble.Builder dribble = Dribble.newBuilder();
        dribble.setDribbleOn(true);
        dribbleSkill.setDribble(dribble);

        module.publish(Exchange.AI_BASIC_SKILL, dribbleSkill.build());
    }
}
