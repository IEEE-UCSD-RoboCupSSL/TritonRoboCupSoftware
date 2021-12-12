package com.triton.module;

import com.triton.Module;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_DETECTION_ROBOTS_BLUE_EXCHANGE;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;

public class VisionDetectionRobotsBlueProcessor extends Module {

    public VisionDetectionRobotsBlueProcessor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) {
        try {
            new VisionDetectionRobotsBlueProcessor();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(SSL_DETECTION_ROBOTS_BLUE_EXCHANGE, this::consume_SSL_DetectionRobotsBlue);
    }

    private void consume_SSL_DetectionRobotsBlue(Object object) {
        List<SSL_DetectionRobot> sslDetectionRobotsBlue = (List<SSL_DetectionRobot>) object;
        for (SSL_DetectionRobot sslDetectionRobotBlue : sslDetectionRobotsBlue) {
            System.out.println(sslDetectionRobotBlue);
        }
    }
}