package com.triton.ai.skills.individual_skills;

import com.triton.module.Module;
import proto.triton.AiBasicSkills;
import proto.triton.AiIndividualSkills;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.triton.messaging.Exchange.*;
import static proto.triton.AiBasicSkills.*;
import static proto.triton.AiIndividualSkills.*;
import static proto.triton.AiIndividualSkills.ChaseBall;
import static proto.vision.MessagesRobocupSslDetection.*;

public class ChaseBallSkill {
    public static void chaseBallSkill(Module module, int id, ChaseBall chaseBall, ArrayList<SSL_DetectionBall> balls) throws IOException {
        float avgBallX = 0;
        float avgBallY = 0;

        for (MessagesRobocupSslDetection.SSL_DetectionBall ball : balls) {
            avgBallX += ball.getX();
            avgBallY += ball.getY();
        }

        avgBallX /= balls.size();
        avgBallY /= balls.size();

        IndividualSkill.Builder pathToPointSkill = IndividualSkill.newBuilder();
        pathToPointSkill.setId(id);
        PathToPoint.Builder pathToPoint = PathToPoint.newBuilder();
        pathToPoint.setX(avgBallX);
        pathToPoint.setY(avgBallY);
        pathToPoint.setFacePoint(true);
        pathToPointSkill.setPathToPoint(pathToPoint);
        module.publish(AI_INDIVIDUAL_SKILL, pathToPointSkill.build());

        BasicSkill.Builder dribbleSkill = BasicSkill.newBuilder();
        dribbleSkill.setId(id);
        Dribble.Builder dribble = Dribble.newBuilder();
        dribble.setDribbleOn(true);
        dribbleSkill.setDribble(dribble);
        module.publish(AI_BASIC_SKILL, dribbleSkill.build());
    }
}
