package com.triton.ai.skills.individual_skills;

import com.triton.messaging.Exchange;
import com.triton.module.Module;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.HashMap;

import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiBasicSkills.Dribble;
import static proto.triton.AiIndividualSkills.*;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class GoalKeepSkill {
    public static void goalKeepSkill(Module module, int id, GoalKeep goalKeep, MessagesRobocupSslGeometry.SSL_GeometryFieldSize field, Ball ball, HashMap<Integer, Robot> allies) throws IOException {
        BasicSkill.Builder dribbleSkill = BasicSkill.newBuilder();
        dribbleSkill.setId(id);
        Dribble.Builder dribble = Dribble.newBuilder();
        dribble.setDribbleOn(true);
        dribbleSkill.setDribble(dribble);
        module.publish(Exchange.AI_BASIC_SKILL, dribbleSkill.build());

        IndividualSkill.Builder pathToPointSkill = IndividualSkill.newBuilder();
        PathToPoint.Builder pathToPoint = PathToPoint.newBuilder();
        float xMin = -1000f;
        float xMax = 1000f;
        float x = Math.min(Math.max(ball.getX(), xMin), xMax);
        float y = -field.getFieldLength() / 2f + 250f;
        pathToPoint.setX(x);
        pathToPoint.setY(y);
        pathToPoint.setFaceX(ball.getX());
        pathToPoint.setFaceY(ball.getY());
        pathToPoint.setFacePoint(true);
        pathToPointSkill.setPathToPoint(pathToPoint);
        module.publish(Exchange.AI_INDIVIDUAL_SKILL, pathToPointSkill.build());
    }
}
