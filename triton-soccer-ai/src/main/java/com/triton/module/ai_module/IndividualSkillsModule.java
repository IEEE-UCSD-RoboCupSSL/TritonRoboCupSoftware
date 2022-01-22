package com.triton.module.ai_module;

import com.rabbitmq.client.Delivery;
import com.triton.ai.skills.individual_skills.*;
import com.triton.module.Module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.AiIndividualSkills.IndividualSkill;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

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

        try {
            switch (individualSkill.getCommandCase()) {
                case GOAL_KEEP -> GoalKeepSkill.goalKeepSkill(this, id, individualSkill.getGoalKeep());
                case PATH_TO_POINT -> PathToPointSkill.pathFindToPointSkill(this, id, individualSkill.getPathToPoint(), allies);
                case CHASE_BALL -> ChaseBallSkill.chaseBallSkill(this, id, individualSkill.getChaseBall(), balls);
                case CATCH_BALL -> CatchBallSkill.catchBallSkill(this, id, individualSkill.getCatchBall());
                case KICK_BALL_TO_POINT -> KickBallToPointSkill.kickBallToPointSkill(this, id, individualSkill.getKickBallToPoint());
                case DRIBBLE_BALL -> DribbleBallSkill.dribbleBallSkill(this, id, individualSkill.getDribbleBall());
                case SHOOT -> ShootSkill.shootSkill(this, id, individualSkill.getShoot());
                case STEAL -> Stealskill.stealSkill(this, id, individualSkill.getSteal());
                case JUKE -> JukeSkill.jukeSkill(this, id, individualSkill.getJuke());
                default -> throw new IllegalStateException("Unexpected value: " + individualSkill.getCommandCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
