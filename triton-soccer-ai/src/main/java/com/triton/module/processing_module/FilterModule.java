package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.ProgramConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import com.triton.util.Vector2d;
import proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static com.triton.util.ObjectHelper.getNearestRobot;
import static com.triton.util.ProtobufUtils.getPos;
import static proto.simulation.SslSimulationRobotFeedback.RobotFeedback;
import static proto.triton.FilteredObject.*;
import static proto.triton.FilteredObject.Ball.CaptureStateCase;
import static proto.triton.FilteredObject.Ball.CaptureStateCase.*;
import static proto.triton.FilteredObject.Ball.newBuilder;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryFieldSize;

public class FilterModule extends Module {
    private static final long DEFAULT_PUBLISH_PERIOD = 10;

    private FilteredWrapperPacket filteredWrapper;
    private Map<Integer, RobotFeedback> feedbacks;

    private Future publishFilteredWrapperFuture;

    public FilterModule(ScheduledThreadPoolExecutor executor) {
        super(executor);
    }

    @Override
    public void run() {
        super.run();
        initDefaultFilteredWrapper();
        publishFilteredWrapperFuture = executor.scheduleAtFixedRate(this::publishFilteredWrapper, 0,
                DEFAULT_PUBLISH_PERIOD, TimeUnit.MILLISECONDS);
    }

    private void initDefaultFilteredWrapper() {
        long timestamp = System.currentTimeMillis();
        FilteredWrapperPacket.Builder filteredWrapper = FilteredWrapperPacket.newBuilder();
        filteredWrapper.setField(initDefaultField());
        filteredWrapper.setBall(initDefaultBall(timestamp));
        filteredWrapper.putAllAllies(initDefaultAllies(timestamp));
        filteredWrapper.putAllFoes(initDefaultFoes(timestamp));
        this.filteredWrapper = filteredWrapper.build();
    }

    private void publishFilteredWrapper() {
        publish(AI_FILTERED_VISION_WRAPPER, filteredWrapper);
    }

    private SSL_GeometryFieldSize initDefaultField() {
        SSL_GeometryFieldSize.Builder defaultField = SSL_GeometryFieldSize.newBuilder();
        defaultField.setFieldLength(0);
        defaultField.setFieldWidth(0);
        defaultField.setGoalWidth(0);
        defaultField.setGoalDepth(0);
        defaultField.setBoundaryWidth(0);
        return defaultField.build();
    }

    private Ball initDefaultBall(long timestamp) {
        Ball.Builder defaultBall = newBuilder();
        defaultBall.setTimestamp(timestamp);
        return defaultBall.build();
    }

    private Map<Integer, Robot> initDefaultAllies(long timestamp) {
        Map<Integer, Robot> defaultFilteredAllies = new HashMap<>();
        for (int id = 0; id < ProgramConstants.gameConfig.numBots; id++) {
            Robot.Builder defaultFilteredAlly = Robot.newBuilder();
            defaultFilteredAlly.setTimestamp(timestamp);
            defaultFilteredAllies.put(id, defaultFilteredAlly.build());
        }
        return defaultFilteredAllies;
    }

    private Map<Integer, Robot> initDefaultFoes(long timestamp) {
        Map<Integer, Robot> defaultFilteredFoes = new HashMap<>();
        for (int id = 0; id < ProgramConstants.gameConfig.numBots; id++) {
            Robot.Builder defaultFilteredFoe = Robot.newBuilder();
            defaultFilteredFoe.setTimestamp(timestamp);
            defaultFilteredFoe.setId(id);
            defaultFilteredFoes.put(id, defaultFilteredFoe.build());
        }
        return defaultFilteredFoes;
    }

    @Override
    protected void prepare() {
        initDefaultFilteredWrapper();
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_FILTERED_VISION_WRAPPER);
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_BIASED_VISION_WRAPPER, this::callbackWrapper);
        declareConsume(AI_ROBOT_FEEDBACKS, this::callbackFeedbacks);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        SSL_WrapperPacket wrapper = (SSL_WrapperPacket) simpleDeserialize(delivery.getBody());

        long timestamp = System.currentTimeMillis();

        FilteredWrapperPacket.Builder filteredWrapper = this.filteredWrapper.toBuilder();
        filteredWrapper.setField(wrapper.getGeometry().getField());
        filteredWrapper.setBall(filterBalls(wrapper.getDetection().getBallsList(), this.filteredWrapper.getBall(),
                feedbacks, this.filteredWrapper.getFoesMap(), timestamp));

        List<SSL_DetectionRobot> allies;
        List<SSL_DetectionRobot> foes;
        if (ProgramConstants.team == Team.YELLOW) {
            allies = wrapper.getDetection().getRobotsYellowList();
            foes = wrapper.getDetection().getRobotsBlueList();
        } else {
            allies = wrapper.getDetection().getRobotsBlueList();
            foes = wrapper.getDetection().getRobotsYellowList();
        }

        filteredWrapper.putAllAllies(filterAllies(allies, this.filteredWrapper.getAlliesMap(), feedbacks, timestamp));
        filteredWrapper.putAllFoes(filterFoes(foes, this.filteredWrapper.getFoesMap(), feedbacks, timestamp));
        this.filteredWrapper = filteredWrapper.build();
    }

    private void callbackFeedbacks(String s, Delivery delivery) {
        feedbacks = (Map<Integer, RobotFeedback>) simpleDeserialize(delivery.getBody());
    }

    private Ball filterBalls(List<SSL_DetectionBall> balls, Ball lastBall, Map<Integer, RobotFeedback> feedbacks,
                             Map<Integer, Robot> lastFoes, long timestamp) {

        CaptureStateCase captureStateCase = FREE;
        int captureId = 0;
        if (feedbacks != null) {
            for (Map.Entry<Integer, RobotFeedback> entry : feedbacks.entrySet()) {
                Integer id = entry.getKey();
                RobotFeedback feedback = entry.getValue();
                if (feedback.getDribblerBallContact()) {
                    captureStateCase = ALLY_CAPTURE;
                    captureId = id;
                }
            }
        }

        if (balls.size() == 0) {
            Ball.Builder lastKnownBall = lastBall.toBuilder();
            lastKnownBall.setTimestamp(timestamp);
            lastKnownBall.setConfidence(0f);

            if (captureStateCase != ALLY_CAPTURE) {
                captureStateCase = FOE_CAPTURE;
                Robot nearestFoe = getNearestRobot(getPos(lastBall), lastFoes.values().stream().toList());
                captureId = nearestFoe.getId();
            }

            switch (captureStateCase) {
                case FREE -> lastKnownBall.setFree(Free.newBuilder().build());
                case ALLY_CAPTURE -> lastKnownBall.setAllyCapture(AllyCapture.newBuilder().setId(captureId).build());
                case FOE_CAPTURE -> lastKnownBall.setFoeCapture(FoeCapture.newBuilder().setId(captureId).build());
            }
            return lastKnownBall.build();
        }

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

        float deltaSeconds = (timestamp - lastBall.getTimestamp()) / 1000f;
        float vx = (x - lastBall.getX()) / deltaSeconds;
        float vy = (y - lastBall.getY()) / deltaSeconds;
        float vz = (z - lastBall.getZ()) / deltaSeconds;
        float accX = (vx - lastBall.getVx()) / deltaSeconds;
        float accY = (vy - lastBall.getVy()) / deltaSeconds;
        float accZ = (vz - lastBall.getAccZ()) / deltaSeconds;

        Ball.Builder filteredBall = lastBall.toBuilder();
        filteredBall.setTimestamp(timestamp);
        filteredBall.setConfidence(1f);
        filteredBall.setX(x);
        filteredBall.setY(y);
        filteredBall.setZ(z);
        filteredBall.setVx(vx);
        filteredBall.setVy(vy);
        filteredBall.setVz(vz);
        filteredBall.setAccX(accX);
        filteredBall.setAccY(accY);
        filteredBall.setAccZ(accZ);

        switch (captureStateCase) {
            case FREE -> filteredBall.setFree(Free.newBuilder().build());
            case ALLY_CAPTURE -> filteredBall.setAllyCapture(AllyCapture.newBuilder().setId(captureId).build());
            case FOE_CAPTURE -> filteredBall.setFoeCapture(FoeCapture.newBuilder().setId(captureId).build());
        }

        return filteredBall.build();
    }

    private Map<Integer, Robot> filterAllies(List<SSL_DetectionRobot> allies,
                                             Map<Integer, Robot> lastAllies,
                                             Map<Integer, RobotFeedback> feedbacks,
                                             long timestamp) {
        Map<Integer, Robot> filteredAllies = new HashMap<>();
        for (SSL_DetectionRobot ally : allies) {
            if (ally.getRobotId() < ProgramConstants.gameConfig.numBots) {
                Robot lastAlly = lastAllies.get(ally.getRobotId());
                boolean hasBall = feedbacks != null && feedbacks.get(ally.getRobotId()).getDribblerBallContact();
                Robot filteredAlly = filterRobot(timestamp, ally, lastAlly, hasBall);
                filteredAllies.put(ally.getRobotId(), filteredAlly);
            }
        }

        return filteredAllies;
    }

    private Map<Integer, Robot> filterFoes(List<SSL_DetectionRobot> foes,
                                           Map<Integer, Robot> lastFoes,
                                           Map<Integer, RobotFeedback> feedbacks,
                                           long timestamp) {
        boolean allyHasBall = false;
        if (feedbacks != null) {
            for (RobotFeedback feedback : feedbacks.values()) {
                if (feedback.getDribblerBallContact()) {
                    allyHasBall = true;
                    break;
                }
            }
        }

        Map<Integer, Robot> filteredFoes = new HashMap<>();
        for (SSL_DetectionRobot foe : foes) {
            if (foe.getRobotId() < ProgramConstants.gameConfig.numBots) {
                Robot lastFoe = lastFoes.get(foe.getRobotId());
                boolean hasBall = false;

                if (!allyHasBall) {
                    // TODO
                }

                Robot filteredFoe = filterRobot(timestamp, foe, lastFoe, hasBall);
                if (allyHasBall)
                    filteredFoe = filteredFoe.toBuilder().setHasBall(false).build();
                filteredFoes.put(foe.getRobotId(), filteredFoe);
            }
        }
        return filteredFoes;
    }

    private Robot filterRobot(long timestamp, SSL_DetectionRobot robot, Robot lastRobot, boolean hasBall) {
        float deltaSeconds = (timestamp - lastRobot.getTimestamp()) / 1000f;
        float vx = (robot.getX() - lastRobot.getX()) / deltaSeconds;
        float vy = (robot.getY() - lastRobot.getY()) / deltaSeconds;
        float angular = Vector2d.angleDifference(robot.getOrientation(), lastRobot.getOrientation()) / deltaSeconds;
        float accX = (vx - lastRobot.getVx()) / deltaSeconds;
        float accY = (vy - lastRobot.getVy()) / deltaSeconds;
        float accAngular = (angular - lastRobot.getAngular()) / deltaSeconds;

        Robot.Builder filteredRobot = lastRobot.toBuilder();
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
        filteredRobot.setHasBall(hasBall);

        if (hasBall && !lastRobot.getHasBall()) {
            filteredRobot.setDribbleStartX(lastRobot.getX());
            filteredRobot.setDribbleStartY(lastRobot.getY());
        }
        return filteredRobot.build();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        publishFilteredWrapperFuture.cancel(false);
    }
}
