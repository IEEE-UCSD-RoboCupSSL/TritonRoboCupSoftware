package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.TritonSoccerAI;
import com.triton.module.Module;
import proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import static proto.vision.MessagesRobocupSslGeometry.*;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class VisionProcessor extends Module {

    public VisionProcessor() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    private static SSL_DetectionRobot convertRobot(SSL_DetectionRobot bot) {
        SSL_DetectionRobot.Builder biasedBot = SSL_DetectionRobot.newBuilder();
        biasedBot.setConfidence(bot.getConfidence());
        biasedBot.setRobotId(bot.getRobotId());

        Vector2f biasedVec = biasVec(bot.getX(), bot.getY());
        biasedBot.setX(biasedVec.getX());
        biasedBot.setY(biasedVec.getY());

        biasedBot.setOrientation(convertOrient(bot.getOrientation()));
        biasedBot.setPixelX(bot.getPixelX());
        biasedBot.setPixelY(bot.getPixelY());
        biasedBot.setHeight(bot.getHeight());
        return biasedBot.build();
    }

    private static Vector2f biasVec(float x, float y) {
        Vector2f.Builder vec = Vector2f.newBuilder();
        vec.setX(x);
        vec.setY(y);
        return biasVec(vec.build());
    }

    private static Vector2f biasVec(Vector2f vec) {
        Vector2f.Builder biasedVec = Vector2f.newBuilder();
        switch (TritonSoccerAI.getTeam()) {
            case YELLOW -> {
                biasedVec.setX(-vec.getY());
                biasedVec.setY(vec.getX());
            }
            case BLUE -> {
                biasedVec.setX(vec.getY());
                biasedVec.setY(-vec.getX());
            }
            default -> throw new IllegalStateException("Unexpected value: " + TritonSoccerAI.getTeam());
        }
        return biasedVec.build();
    }

    private static float convertOrient(float orient) {
        switch (TritonSoccerAI.getTeam()) {
            case YELLOW -> {
                return (float) ((orient + (Math.PI / 2)) % (2 * Math.PI));
            }
            case BLUE -> {
                return (float) ((orient - (Math.PI / 2)) % (2 * Math.PI));
            }
            default -> throw new IllegalStateException("Unexpected value: " + TritonSoccerAI.getTeam());
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(RAW_WRAPPER_PACKAGE, this::callbackRawWrapperPacket);
        declarePublish(BIASED_FIELD);
        declarePublish(BIASED_BALLS);
        declarePublish(BIASED_ALLIES);
        declarePublish(BIASED_FOES);
    }

    private void callbackRawWrapperPacket(String s, Delivery delivery) {
        SSL_WrapperPacket wrapperPacket = null;
        try {
            wrapperPacket = (SSL_WrapperPacket) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (wrapperPacket.hasGeometry() && wrapperPacket.getGeometry().hasField()) {
            try {
                publish(BIASED_FIELD, convertField(wrapperPacket.getGeometry().getField()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (wrapperPacket.hasDetection()) {
            try {
                SSL_DetectionFrame detection = wrapperPacket.getDetection();
                publish(BIASED_BALLS, convertBalls(detection.getBallsList()));

                List<SSL_DetectionRobot> yellows = detection.getRobotsYellowList();
                List<SSL_DetectionRobot> blues = detection.getRobotsBlueList();
                List<List<SSL_DetectionRobot>> biasedBots = convertRobots(yellows, blues);
                publish(BIASED_ALLIES, biasedBots.get(0));
                publish(BIASED_FOES, biasedBots.get(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private SSL_GeometryFieldSize convertField(SSL_GeometryFieldSize field) {
        SSL_GeometryFieldSize.Builder biasedField = SSL_GeometryFieldSize.newBuilder();
        biasedField.setFieldLength(field.getFieldLength());
        biasedField.setFieldWidth(field.getFieldWidth());
        biasedField.setGoalWidth(field.getGoalWidth());
        biasedField.setGoalDepth(field.getGoalDepth());
        biasedField.setBoundaryWidth(field.getBoundaryWidth());

        for (SSL_FieldLineSegment line : field.getFieldLinesList()) {
            SSL_FieldLineSegment.Builder biasedLine = SSL_FieldLineSegment.newBuilder();

            biasedLine.setName(line.getName());
            biasedLine.setP1(biasVec(line.getP1()));
            biasedLine.setP2(biasVec(line.getP2()));
            biasedLine.setThickness(line.getThickness());
            biasedLine.setType(line.getType());

            biasedField.addFieldLines(biasedLine);
        }

        return biasedField.build();
    }

    private List<SSL_DetectionBall> convertBalls(List<SSL_DetectionBall> balls) {
        List<SSL_DetectionBall> biasedBalls = new ArrayList<>();
        for (SSL_DetectionBall ball : balls) {
            SSL_DetectionBall.Builder biasedBall = SSL_DetectionBall.newBuilder();
            biasedBall.setConfidence(ball.getConfidence());
            biasedBall.setArea(ball.getArea());

            Vector2f biasedVec = biasVec(ball.getX(), ball.getY());
            biasedBall.setX(biasedVec.getX());
            biasedBall.setY(biasedVec.getY());
            biasedBall.setZ(ball.getZ());

            biasedBall.setPixelX(ball.getPixelX());
            biasedBall.setPixelY(ball.getPixelY());

            biasedBalls.add(biasedBall.build());
        }

        return biasedBalls;
    }

    private List<List<SSL_DetectionRobot>> convertRobots(List<SSL_DetectionRobot> yellows, List<SSL_DetectionRobot> blues) {
        List<List<SSL_DetectionRobot>> biasedBots = new ArrayList<>();
        List<SSL_DetectionRobot> biasedAllies = new ArrayList<>();
        List<SSL_DetectionRobot> biasedFoes = new ArrayList<>();

        List<SSL_DetectionRobot> allies;
        List<SSL_DetectionRobot> foes;
        switch (TritonSoccerAI.getTeam()) {
            case YELLOW -> {
                allies = yellows;
                foes = blues;
            }
            case BLUE -> {
                allies = blues;
                foes = yellows;
            }
            default -> throw new IllegalStateException("Unexpected value: " + TritonSoccerAI.getTeam());
        }

        for (SSL_DetectionRobot ally : allies) {
            SSL_DetectionRobot biasedAlly = convertRobot(ally);
            biasedAllies.add(biasedAlly);
        }

        for (SSL_DetectionRobot foe : foes) {
            SSL_DetectionRobot biasedFoe = convertRobot(foe);
            biasedFoes.add(biasedFoe);
        }

        biasedBots.add(biasedAllies);
        biasedBots.add(biasedFoes);
        return biasedBots;
    }
}
