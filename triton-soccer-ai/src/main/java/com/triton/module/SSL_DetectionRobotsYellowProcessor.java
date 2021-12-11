package com.triton.module;

import com.triton.PublisherConsumer.Module;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.PublisherConsumer.Exchange.SSL_DETECTION_FRAME_EXCHANGE;
import static com.triton.PublisherConsumer.Exchange.SSL_DETECTION_ROBOTS_YELLOW_EXCHANGE;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;

public class SSL_DetectionRobotsYellowProcessor extends Module {

    public SSL_DetectionRobotsYellowProcessor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) {
        try {
            new SSL_DetectionRobotsYellowProcessor();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupRabbitMQ() throws IOException, TimeoutException {
        super.setupRabbitMQ();

        declareConsume(SSL_DETECTION_ROBOTS_YELLOW_EXCHANGE, this::consume_SSL_DetectionRobotsYellow, null);
    }

    public void consume_SSL_DetectionRobotsYellow(Object object) {
        List<MessagesRobocupSslDetection.SSL_DetectionRobot> sslDetectionRobotsYellow = (List<MessagesRobocupSslDetection.SSL_DetectionRobot>) object;
        for (MessagesRobocupSslDetection.SSL_DetectionRobot sslDetectionRobotYellow : sslDetectionRobotsYellow) {
            System.out.println(sslDetectionRobotYellow);
        }
    }
}