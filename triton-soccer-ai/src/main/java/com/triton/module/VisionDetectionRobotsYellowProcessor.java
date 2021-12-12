package com.triton.module;

import com.triton.Module;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_DETECTION_ROBOTS_YELLOW_EXCHANGE;

public class VisionDetectionRobotsYellowProcessor extends Module {

    public VisionDetectionRobotsYellowProcessor() throws IOException, TimeoutException {
        super();
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
        List<MessagesRobocupSslDetection.SSL_DetectionRobot> sslDetectionRobotsYellow = (List<MessagesRobocupSslDetection.SSL_DetectionRobot>) object;
        for (MessagesRobocupSslDetection.SSL_DetectionRobot sslDetectionRobotYellow : sslDetectionRobotsYellow) {
            System.out.println(sslDetectionRobotYellow);
        }
    }
}