package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.module.Module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class FilterModule extends Module {
    private LinkedList<ArrayList<SSL_DetectionBall>> aggregatedBalls;
    private HashMap<Integer, LinkedList<SSL_DetectionRobot>> aggregatedAllies;
    private HashMap<Integer, LinkedList<SSL_DetectionRobot>> aggregatedFoes;

    public FilterModule() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void prepare() {
        super.prepare();

        aggregatedBalls = new LinkedList<>();
        aggregatedAllies = new HashMap<>();
        aggregatedFoes = new HashMap<>();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_BALLS, this::callbackBalls);
        declareConsume(AI_BIASED_ALLIES, this::callbackAllies);
        declareConsume(AI_BIASED_FOES, this::callbackFoes);

        declarePublish(AI_FILTERED_BIASED_BALLS);
        declarePublish(AI_FILTERED_BIASED_ALLIES);
        declarePublish(AI_FILTERED_BIASED_FOES);
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

        publish(AI_FILTERED_BIASED_BALLS, filteredBall.build());
    }

    private void callbackAllies(String s, Delivery delivery) {
        HashMap<Integer, SSL_DetectionRobot> allies = (HashMap<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());

        HashMap<Integer, Robot> filteredAllies = new HashMap<>();
        for (SSL_DetectionRobot ally : allies.values()) {
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

        publish(AI_FILTERED_BIASED_ALLIES, filteredAllies);
    }

    private void callbackFoes(String s, Delivery delivery) {
        HashMap<Integer, SSL_DetectionRobot> foes = (HashMap<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());

        HashMap<Integer, Robot> filteredFoes = new HashMap<>();
        for (SSL_DetectionRobot foe : foes.values()) {
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

        publish(AI_FILTERED_BIASED_FOES, filteredFoes);
    }
}
