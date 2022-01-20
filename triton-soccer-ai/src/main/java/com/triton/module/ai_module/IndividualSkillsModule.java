package com.triton.module.ai_module;

import com.rabbitmq.client.Delivery;
import com.triton.ai.skills.individual_skills.*;
import com.triton.module.Module;
import proto.vision.MessagesRobocupSslDetection;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.AiBasicSkills.BasicSkill;
import static proto.triton.AiIndividualSkills.IndividualSkill;
import static proto.vision.MessagesRobocupSslDetection.*;
import static proto.vision.MessagesRobocupSslGeometry.*;

public class IndividualSkillsModule extends Module {
    SSL_GeometryFieldSize field;
    HashMap<Integer, SSL_DetectionRobot> allies;
    HashMap<Integer, SSL_DetectionRobot> foes;
    ArrayList<SSL_DetectionBall> balls;

    public IndividualSkillsModule() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_FIELD, this::callbackField);
        declareConsume(AI_BIASED_ALLIES, this::callbackAllies);
        declareConsume(AI_BIASED_FOES, this::callbackFoes);
        declareConsume(AI_BIASED_BALLS, this::callbackBalls);
        declareConsume(AI_INDIVIDUAL_SKILL, this::callbackIndividualSkill);

        declarePublish(AI_BASIC_SKILL);
    }

    private void callbackField(String s, Delivery delivery) {
        SSL_GeometryFieldSize field;
        try {
            field = (SSL_GeometryFieldSize) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        this.field = field;
    }

    private void callbackAllies(String s, Delivery delivery) {
        HashMap<Integer, SSL_DetectionRobot> allies;
        try {
            allies = (HashMap<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        this.allies = allies;
    }

    private void callbackFoes(String s, Delivery delivery) {
        HashMap<Integer, SSL_DetectionRobot> foes;
        try {
            foes = (HashMap<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        this.foes = foes;
    }

    private void callbackBalls(String s, Delivery delivery) {
        ArrayList<SSL_DetectionBall> balls;
        try {
            balls = (ArrayList<SSL_DetectionBall>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        this.balls = balls;
    }

    private void callbackIndividualSkill(String s, Delivery delivery) {
        IndividualSkill individualSkill;
        try {
            individualSkill = (IndividualSkill) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        int id = individualSkill.getId();

        BasicSkill basicSkill = null;
        switch (individualSkill.getCommandCase()) {
            case GOAL_KEEP -> basicSkill = GoalKeepSkill.goalKeepSkill(id, individualSkill.getGoalKeep());
            case PATHFIND_TO_POINT -> basicSkill = PathFindToPointSkill.pathFindToPointSkill(id, individualSkill.getPathfindToPoint());
            case CHASE_BALL -> basicSkill = ChaseBallSkill.chaseBallSkill(id, individualSkill.getChaseBall());
            case CATCH_BALL -> basicSkill = CatchBallSkill.catchBallSkill(id, individualSkill.getCatchBall());
            case KICK_BALL_TO_POINT -> basicSkill = KickBallToPointSkill.kickBallToPointSkill(id, individualSkill.getKickBallToPoint());
            case DRIBBLE_BALL -> basicSkill = DribbleBallSkill.dribbleBallSkill(id, individualSkill.getDribbleBall());
            case SHOOT -> basicSkill = ShootSkill.shootSkill(id, individualSkill.getShoot());
            case STEAL -> basicSkill = Stealskill.stealSkill(id, individualSkill.getSteal());
            case JUKE -> basicSkill = JukeSkill.jukeSkill(id, individualSkill.getJuke());
            default -> throw new IllegalStateException("Unexpected value: " + individualSkill.getCommandCase());
        }

        if (basicSkill != null) {
            try {
                publish(AI_BASIC_SKILL, basicSkill);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}