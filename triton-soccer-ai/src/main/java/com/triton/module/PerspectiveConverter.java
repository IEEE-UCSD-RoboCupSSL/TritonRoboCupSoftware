package com.triton.module;

import com.triton.TritonSoccerAI;
import proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.*;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import static proto.vision.MessagesRobocupSslGeometry.*;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class PerspectiveConverter extends Module {

    public PerspectiveConverter() throws IOException, TimeoutException {
        declareConsume(RAW_WRAPPER_PACKAGE, this::consumeRawWrapperPacket);
        declarePublish(PERSPECTIVE_FIELD);
        declarePublish(PERSPECTIVE_BALLS);
        declarePublish(PERSPECTIVE_ROBOTS_ALLY);
        declarePublish(PERSPECTIVE_ROBOTS_FOE);
    }

    private void consumeRawWrapperPacket(Object o) {
        SSL_WrapperPacket wrapperPacket = (SSL_WrapperPacket) o;
        SSL_GeometryData geometryData = wrapperPacket.getGeometry();
        SSL_DetectionFrame detectionFrame = wrapperPacket.getDetection();
        List<SSL_DetectionBall> balls = detectionFrame.getBallsList();
        List<SSL_DetectionRobot> robotsYellow = detectionFrame.getRobotsYellowList();
        List<SSL_DetectionRobot> robotsBlue = detectionFrame.getRobotsBlueList();

        SSL_GeometryFieldSize field = geometryData.getField();
        try {
            publish(PERSPECTIVE_FIELD, convertField(field));
            publish(PERSPECTIVE_BALLS, convertBalls(balls));
            publish(PERSPECTIVE_ROBOTS_ALLY, convertAllies(robotsYellow, robotsBlue));
            publish(PERSPECTIVE_ROBOTS_FOE, convertFoe(robotsYellow, robotsBlue));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SSL_GeometryFieldSize convertField(SSL_GeometryFieldSize field) {
        SSL_GeometryFieldSize.Builder perspectiveField = SSL_GeometryFieldSize.newBuilder();
        perspectiveField.setFieldLength(field.getFieldLength());
        perspectiveField.setFieldWidth(field.getFieldWidth());
        perspectiveField.setGoalWidth(field.getGoalWidth());
        perspectiveField.setGoalDepth(field.getGoalDepth());
        perspectiveField.setBoundaryWidth(field.getBoundaryWidth());

        for (SSL_FieldLineSegment line : field.getFieldLinesList()) {
            SSL_FieldLineSegment.Builder perspectiveLine = SSL_FieldLineSegment.newBuilder();

            perspectiveLine.setName(line.getName());
            perspectiveLine.setP1(convertVector(line.getP1()));
            perspectiveLine.setP2(convertVector(line.getP2()));
            perspectiveLine.setThickness(line.getThickness());
            perspectiveLine.setType(line.getType());

            perspectiveField.addFieldLines(perspectiveLine);
        }

        return perspectiveField.build();
    }

    private List<SSL_DetectionBall> convertBalls(List<SSL_DetectionBall> balls) {
        List<SSL_DetectionBall> perspectiveBalls = new ArrayList<SSL_DetectionBall>();
        for (SSL_DetectionBall ball : balls) {
            SSL_DetectionBall.Builder perspectiveBall = SSL_DetectionBall.newBuilder();
            perspectiveBall.setConfidence(ball.getConfidence());
            perspectiveBall.setArea(ball.getArea());

            Vector2f.Builder ballVector = Vector2f.newBuilder();
            ballVector.setX(ball.getX());
            ballVector.setY(ball.getY());
            Vector2f perspectiveVector = convertVector(ballVector.build());
            perspectiveBall.setX(perspectiveVector.getX());
            perspectiveBall.setY(perspectiveVector.getY());
            perspectiveBall.setZ(ball.getZ());
            perspectiveBall.setPixelX(ball.getPixelX());
            perspectiveBall.setPixelY(ball.getPixelY());

            perspectiveBalls.add(perspectiveBall.build());
        }

        return perspectiveBalls;
    }

    private List<SSL_DetectionRobot> convertAllies(List<SSL_DetectionRobot> robotsYellow, List<SSL_DetectionRobot> robotsBlue) {
        return null;
    }

    private List<SSL_DetectionRobot> convertFoe(List<SSL_DetectionRobot> robotsYellow, List<SSL_DetectionRobot> robotsBlue) {
        return null;
    }

    private Vector2f convertVector(Vector2f vector) {
        Vector2f.Builder perspectiveVector = Vector2f.newBuilder();
        switch (TritonSoccerAI.getTeam()) {
            case YELLOW -> {
                perspectiveVector.setX(-vector.getY());
                perspectiveVector.setY(vector.getX());
            }
            case BLUE -> {
                perspectiveVector.setX(vector.getY());
                perspectiveVector.setY(-vector.getX());
            }
        }
        return perspectiveVector.build();
    }
}
