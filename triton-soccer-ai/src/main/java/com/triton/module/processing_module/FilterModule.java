package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.module.Module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class FilterModule extends Module {
    private Ball filteredBall;
    private Map<Integer, Robot> filteredAllies;
    private Map<Integer, Robot> filteredFoes;

    public FilterModule(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    public void run() {
        super.run();
        initDefaults();
    }

    private void initDefaults() {
        Ball.Builder ball = Ball.newBuilder();
        ball.setX(0);
        ball.setY(0);
        ball.setZ(0);
        ball.setVx(0);
        ball.setVy(0);
        ball.setVz(0);
        filteredBall = ball.build();

        for (int id = 0; id < RuntimeConstants.gameConfig.numBots; id++) {
            Robot.Builder filteredAlly = Robot.newBuilder();
            filteredAlly.setId(id);
            filteredAlly.setX(0);
            filteredAlly.setY(0);
            filteredAlly.setOrientation(0);
            filteredAlly.setVx(0);
            filteredAlly.setVy(0);
            filteredAlly.setAngular(0);
            filteredAllies.put(id, filteredAlly.build());
        }

        for (int id = 0; id < RuntimeConstants.gameConfig.numBots; id++) {
            Robot.Builder filteredFoe = Robot.newBuilder();
            filteredFoe.setId(id);
            filteredFoe.setX(0);
            filteredFoe.setY(0);
            filteredFoe.setOrientation(0);
            filteredFoe.setVx(0);
            filteredFoe.setVy(0);
            filteredFoe.setAngular(0);
            filteredFoes.put(id, filteredFoe.build());
        }

        publish(AI_FILTERED_BALL, filteredBall);
        publish(AI_FILTERED_ALLIES, filteredAllies);
        publish(AI_FILTERED_FOES, filteredFoes);
    }

    @Override
    protected void prepare() {
        filteredBall = Ball.getDefaultInstance();
        filteredAllies = new HashMap<>();
        filteredFoes = new HashMap<>();
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_FILTERED_BALL);
        declarePublish(AI_FILTERED_ALLIES);
        declarePublish(AI_FILTERED_FOES);
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_BIASED_BALLS, this::callbackBalls);
        declareConsume(AI_BIASED_ALLIES, this::callbackAllies);
        declareConsume(AI_BIASED_FOES, this::callbackFoes);
    }

    private void callbackBalls(String s, Delivery delivery) {
        ArrayList<SSL_DetectionBall> balls = (ArrayList<SSL_DetectionBall>) simpleDeserialize(delivery.getBody());

        if (balls.size() == 0) return;

        Ball.Builder filteredBall = Ball.newBuilder();

        float x = 0;
        float y = 0;
        float z = 0;
        for (SSL_DetectionBall ball : balls) {
            x += ball.getX();
            y += ball.getY();
            z += ball.getZ();
        }

        filteredBall.setX(x / balls.size());
        filteredBall.setY(y / balls.size());
        filteredBall.setZ(z / balls.size());
        filteredBall.setVx(0);
        filteredBall.setVy(0);
        filteredBall.setVz(0);

        this.filteredBall = filteredBall.build();

        publish(AI_FILTERED_BALL, this.filteredBall);
    }

    private void callbackAllies(String s, Delivery delivery) {
        Map<Integer, SSL_DetectionRobot> allies = (Map<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());

        for (SSL_DetectionRobot ally : allies.values()) {
            if (ally.getRobotId() > RuntimeConstants.gameConfig.numBots - 1) continue;
            Robot.Builder filteredAlly = Robot.newBuilder();
            filteredAlly.setId(ally.getRobotId());
            filteredAlly.setX(ally.getX());
            filteredAlly.setY(ally.getY());
            filteredAlly.setOrientation(ally.getOrientation());
            filteredAlly.setVx(0);
            filteredAlly.setVy(0);
            filteredAlly.setAngular(0);
            filteredAllies.put(ally.getRobotId(), filteredAlly.build());
        }

        publish(AI_FILTERED_ALLIES, filteredAllies);
    }

    private void callbackFoes(String s, Delivery delivery) {
        Map<Integer, SSL_DetectionRobot> foes = (Map<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());

        for (SSL_DetectionRobot foe : foes.values()) {
            if (foe.getRobotId() > RuntimeConstants.gameConfig.numBots - 1) continue;
            Robot.Builder filteredFoe = Robot.newBuilder();
            filteredFoe.setId(foe.getRobotId());
            filteredFoe.setX(foe.getX());
            filteredFoe.setY(foe.getY());
            filteredFoe.setOrientation(foe.getOrientation());
            filteredFoe.setVx(0);
            filteredFoe.setVy(0);
            filteredFoe.setAngular(0);
            filteredFoes.put(foe.getRobotId(), filteredFoe.build());
        }

        publish(AI_FILTERED_FOES, filteredFoes);
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }
}
