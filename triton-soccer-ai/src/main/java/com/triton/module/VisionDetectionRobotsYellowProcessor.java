package com.triton.module;

import com.triton.Module;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_DETECTION_ROBOTS_YELLOW_EXCHANGE;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class VisionDetectionRobotsYellowProcessor extends Module {

    public VisionDetectionRobotsYellowProcessor() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    public static void main(String[] args) {
        try {
            new VisionDetectionRobotsYellowProcessor();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(SSL_DETECTION_ROBOTS_YELLOW_EXCHANGE, this::consume_SSL_DetectionRobotsYellow);
    }

    private void consume_SSL_DetectionRobotsYellow(Object object) {
        List<SSL_DetectionRobot> sslDetectionRobotsYellow = (List<SSL_DetectionRobot>) object;
        for (SSL_DetectionRobot sslDetectionRobotYellow : sslDetectionRobotsYellow) {
            System.out.println(sslDetectionRobotYellow);
        }
    }
}