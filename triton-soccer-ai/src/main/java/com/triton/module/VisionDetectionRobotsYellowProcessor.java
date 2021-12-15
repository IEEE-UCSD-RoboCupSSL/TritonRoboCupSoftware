package com.triton.module;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.RAW_ROBOTS_YELLOW;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class VisionDetectionRobotsYellowProcessor extends Module {

    public VisionDetectionRobotsYellowProcessor() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(RAW_ROBOTS_YELLOW, this::consume_SSL_DetectionRobotsYellow);
    }

    private void consume_SSL_DetectionRobotsYellow(Object object) {
        List<SSL_DetectionRobot> sslDetectionRobotsYellow = (List<SSL_DetectionRobot>) object;
        for (SSL_DetectionRobot sslDetectionRobotYellow : sslDetectionRobotsYellow) {
            System.out.println(sslDetectionRobotYellow);
        }
    }
}