package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import com.triton.util.ConvertCoordinate;
import proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import static proto.vision.MessagesRobocupSslGeometry.*;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class VisionBiasedConverter extends Module {

    public VisionBiasedConverter() {
        super();
    }

    private static SSL_GeometryFieldSize audienceToBiased(SSL_GeometryFieldSize field) {
        SSL_GeometryFieldSize.Builder biasedField = field.toBuilder();

        biasedField.clearFieldLines();
        for (SSL_FieldLineSegment fieldLine : field.getFieldLinesList())
            biasedField.addFieldLines(audienceToBiased(fieldLine));

        biasedField.clearFieldArcs();
        for (SSL_FieldCircularArc fieldArc : field.getFieldArcsList())
            biasedField.addFieldArcs(audienceToBiased(fieldArc));

        return biasedField.build();
    }

    private static SSL_FieldLineSegment audienceToBiased(SSL_FieldLineSegment fieldLine) {
        SSL_FieldLineSegment.Builder biasedFieldLine = fieldLine.toBuilder();
        biasedFieldLine.setP1(audienceToBiased(fieldLine.getP1()));
        biasedFieldLine.setP2(audienceToBiased(fieldLine.getP2()));
        return biasedFieldLine.build();
    }

    private static Vector2f audienceToBiased(Vector2f vector) {
        Vector2f.Builder biasedVector = vector.toBuilder();
        List<Float> floatList = ConvertCoordinate.audienceToBiased(vector.getX(), vector.getY());
        biasedVector.setX(floatList.get(0));
        biasedVector.setY(floatList.get(1));
        return biasedVector.build();
    }

    private static SSL_FieldCircularArc audienceToBiased(SSL_FieldCircularArc fieldCircularArc) {
        SSL_FieldCircularArc.Builder biasedFieldCircularArc = fieldCircularArc.toBuilder();
        biasedFieldCircularArc.setCenter(audienceToBiased(fieldCircularArc.getCenter()));
//        biasedFieldCircularArc.setA1(ConvertCoordinate.audienceToBiased(fieldCircularArc.getA1()));
//        biasedFieldCircularArc.setA2(ConvertCoordinate.audienceToBiased(fieldCircularArc.getA2()));
        biasedFieldCircularArc.setA1(0);
        biasedFieldCircularArc.setA2(360);
        return biasedFieldCircularArc.build();
    }

    private static SSL_DetectionBall audienceToBiased(SSL_DetectionBall ball) {
        SSL_DetectionBall.Builder biasedBall = ball.toBuilder();

        List<Float> biasedPosition = ConvertCoordinate.audienceToBiased(ball.getX(), ball.getY());
        biasedBall.setX(biasedPosition.get(0));
        biasedBall.setY(biasedPosition.get(1));

        List<Float> biasedPixel = ConvertCoordinate.audienceToBiased(ball.getPixelX(), ball.getPixelY());
        biasedBall.setPixelX(biasedPixel.get(0));
        biasedBall.setPixelY(biasedPixel.get(1));

        return biasedBall.build();
    }

    private static SSL_DetectionRobot audienceToBiased(SSL_DetectionRobot robot) {
        SSL_DetectionRobot.Builder biasedRobot = robot.toBuilder();

        List<Float> biasedPosition = ConvertCoordinate.audienceToBiased(robot.getX(), robot.getY());
        biasedRobot.setX(biasedPosition.get(0));
        biasedRobot.setY(biasedPosition.get(1));

        biasedRobot.setOrientation(ConvertCoordinate.audienceToBiased(robot.getOrientation()));

        List<Float> biasedPixel = ConvertCoordinate.audienceToBiased(robot.getPixelX(), robot.getPixelY());
        biasedRobot.setPixelX(biasedPixel.get(0));
        biasedRobot.setPixelY(biasedPixel.get(1));

        return biasedRobot.build();
    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {
        declarePublish(AI_BIASED_FIELD);
        declarePublish(AI_BIASED_BALLS);
        declarePublish(AI_BIASED_ALLIES);
        declarePublish(AI_BIASED_FOES);
    }

    @Override
    protected void declareConsumes() throws IOException, TimeoutException {
        declareConsume(AI_VISION_WRAPPER, this::callbackWrapper);
    }

    private void callbackWrapper(String s, Delivery delivery) {
        SSL_WrapperPacket wrapper = (SSL_WrapperPacket) simpleDeserialize(delivery.getBody());

        if (wrapper.hasGeometry() && wrapper.getGeometry().hasField())
            publish(AI_BIASED_FIELD, audienceToBiased(wrapper.getGeometry().getField()));

        if (wrapper.hasDetection()) {
            SSL_DetectionFrame detection = wrapper.getDetection();

            ArrayList<SSL_DetectionBall> biasedBalls = new ArrayList<>();
            for (SSL_DetectionBall ball : detection.getBallsList())
                biasedBalls.add(audienceToBiased(ball));
            publish(AI_BIASED_BALLS, biasedBalls);

            HashMap<Integer, SSL_DetectionRobot> biasedYellows = new HashMap<>();
            for (SSL_DetectionRobot robot : detection.getRobotsYellowList())
                biasedYellows.put(robot.getRobotId(), audienceToBiased(robot));

            HashMap<Integer, SSL_DetectionRobot> biasedBlues = new HashMap<>();
            for (SSL_DetectionRobot robot : detection.getRobotsBlueList())
                biasedBlues.put(robot.getRobotId(), audienceToBiased(robot));

            HashMap<Integer, SSL_DetectionRobot> biasedAllies;
            HashMap<Integer, SSL_DetectionRobot> biasedFoes;
            if (RuntimeConstants.team == Team.YELLOW) {
                biasedAllies = biasedYellows;
                biasedFoes = biasedBlues;
            } else {
                biasedAllies = biasedBlues;
                biasedFoes = biasedYellows;
            }

            publish(AI_BIASED_ALLIES, biasedAllies);
            publish(AI_BIASED_FOES, biasedFoes);
        }
    }
}
