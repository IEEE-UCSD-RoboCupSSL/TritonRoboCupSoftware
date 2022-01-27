package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.module.Module;
import com.triton.util.Vector2d;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class FilterModule extends Module {
    private static final long DEFAULT_PUBLISH_PERIOD = 10;

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
        executor.scheduleAtFixedRate(this::publishFilteredObjects, 0, DEFAULT_PUBLISH_PERIOD, TimeUnit.MILLISECONDS);
    }

    private void initDefaults() {
        long timestamp = System.currentTimeMillis();

        Ball.Builder ball = Ball.newBuilder();
        ball.setTimestamp(timestamp);
        ball.setX(0);
        ball.setY(0);
        ball.setZ(0);
        ball.setVx(0);
        ball.setVy(0);
        ball.setVz(0);
        ball.setAccX(0);
        ball.setAccY(0);
        ball.setAccZ(0);
        filteredBall = ball.build();

        for (int id = 0; id < RuntimeConstants.gameConfig.numBots; id++) {
            Robot.Builder filteredAlly = Robot.newBuilder();
            filteredAlly.setTimestamp(timestamp);
            filteredAlly.setId(id);
            filteredAlly.setX(0);
            filteredAlly.setY(0);
            filteredAlly.setOrientation(0);
            filteredAlly.setVx(0);
            filteredAlly.setVy(0);
            filteredAlly.setAngular(0);
            filteredAlly.setAccX(0);
            filteredAlly.setAccY(0);
            filteredAlly.setAccAngular(0);
            filteredAllies.put(id, filteredAlly.build());
        }

        for (int id = 0; id < RuntimeConstants.gameConfig.numBots; id++) {
            Robot.Builder filteredFoe = Robot.newBuilder();
            filteredFoe.setTimestamp(timestamp);
            filteredFoe.setId(id);
            filteredFoe.setX(0);
            filteredFoe.setY(0);
            filteredFoe.setOrientation(0);
            filteredFoe.setVx(0);
            filteredFoe.setVy(0);
            filteredFoe.setAngular(0);
            filteredFoe.setAccX(0);
            filteredFoe.setAccY(0);
            filteredFoe.setAccAngular(0);
            filteredFoes.put(id, filteredFoe.build());
        }
    }

    private void publishFilteredObjects() {
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

        float x = 0;
        float y = 0;
        float z = 0;
        for (SSL_DetectionBall ball : balls) {
            x += ball.getX();
            y += ball.getY();
            z += ball.getZ();
        }
        x /= balls.size();
        y /= balls.size();
        z /= balls.size();

        long timestamp = System.currentTimeMillis();
        float deltaSeconds = (timestamp - filteredBall.getTimestamp()) / 1000f;
        float vx = (x - filteredBall.getX()) / deltaSeconds;
        float vy = (y - filteredBall.getY()) / deltaSeconds;
        float vz = (z - filteredBall.getZ()) / deltaSeconds;
        float accX = (vx - filteredBall.getVx()) / deltaSeconds;
        float accY = (vy - filteredBall.getVy()) / deltaSeconds;
        float accZ = (vz - filteredBall.getAccZ()) / deltaSeconds;

        Ball.Builder filteredBall = Ball.newBuilder();
        filteredBall.setTimestamp(timestamp);
        filteredBall.setX(x);
        filteredBall.setY(y);
        filteredBall.setZ(z);
        filteredBall.setVx(vx);
        filteredBall.setVy(vy);
        filteredBall.setVz(vz);
        filteredBall.setAccX(accX);
        filteredBall.setAccY(accY);
        filteredBall.setAccZ(accZ);

        this.filteredBall = filteredBall.build();
    }

    private void callbackAllies(String s, Delivery delivery) {
        Map<Integer, SSL_DetectionRobot> allies = (Map<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
        long timestamp = System.currentTimeMillis();
        for (SSL_DetectionRobot ally : allies.values()) {
            if (ally.getRobotId() < RuntimeConstants.gameConfig.numBots) {
                Robot lastAlly = filteredAllies.get(ally.getRobotId());
                Robot filteredAlly = createFilteredRobot(timestamp, ally, lastAlly);
                filteredAllies.put(ally.getRobotId(), filteredAlly);
            }
        }
    }

    private void callbackFoes(String s, Delivery delivery) {
        Map<Integer, SSL_DetectionRobot> foes = (Map<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
        long timestamp = System.currentTimeMillis();
        for (SSL_DetectionRobot foe : foes.values()) {
            if (foe.getRobotId() < RuntimeConstants.gameConfig.numBots) {
                Robot lastFoe = filteredFoes.get(foe.getRobotId());
                Robot filteredFoe = createFilteredRobot(timestamp, foe, lastFoe);
                filteredFoes.put(foe.getRobotId(), filteredFoe);
            }
        }
    }

    private Robot createFilteredRobot(long timestamp, SSL_DetectionRobot robot, Robot lastRobot) {
        float deltaSeconds = (timestamp - lastRobot.getTimestamp()) / 1000f;
        float vx = (robot.getX() - lastRobot.getX()) / deltaSeconds;
        float vy = (robot.getY() - lastRobot.getY()) / deltaSeconds;
        float angular = Vector2d.angleDifference(robot.getOrientation(), lastRobot.getOrientation()) / deltaSeconds;
        float accX = (vx - lastRobot.getVx()) / deltaSeconds;
        float accY = (vy - lastRobot.getVy()) / deltaSeconds;
        float accAngular = (angular - lastRobot.getAngular()) / deltaSeconds;

        Robot.Builder filteredRobot = Robot.newBuilder();
        filteredRobot.setTimestamp(timestamp);
        filteredRobot.setId(robot.getRobotId());
        filteredRobot.setX(robot.getX());
        filteredRobot.setY(robot.getY());
        filteredRobot.setOrientation(robot.getOrientation());
        filteredRobot.setVx(vx);
        filteredRobot.setVy(vy);
        filteredRobot.setAngular(angular);
        filteredRobot.setAccX(accX);
        filteredRobot.setAccY(accY);
        filteredRobot.setAccAngular(accAngular);
        return filteredRobot.build();
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }
}
