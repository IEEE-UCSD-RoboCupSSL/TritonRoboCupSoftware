package com.triton.module;

import com.triton.Module;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_DETECTION_ROBOTS_BLUE_EXCHANGE;

public class SSL_DetectionRobotsBlueProcessor extends Module {

    public SSL_DetectionRobotsBlueProcessor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) {
        try {
            new SSL_DetectionRobotsBlueProcessor();
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
        List<MessagesRobocupSslDetection.SSL_DetectionRobot> sslDetectionRobotsBlue = (List<MessagesRobocupSslDetection.SSL_DetectionRobot>) object;
        for (MessagesRobocupSslDetection.SSL_DetectionRobot sslDetectionRobotBlue : sslDetectionRobotsBlue) {
            System.out.println(sslDetectionRobotBlue);
        }
    }
}