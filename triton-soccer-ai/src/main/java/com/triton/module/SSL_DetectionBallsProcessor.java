package com.triton.module;

import com.triton.PublisherConsumer.Module;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.PublisherConsumer.Exchange.SSL_DETECTION_BALLS_EXCHANGE;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;

public class SSL_DetectionBallsProcessor extends Module {

    public SSL_DetectionBallsProcessor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) {
        try {
            new SSL_DetectionBallsProcessor();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupRabbitMQ() throws IOException, TimeoutException {
        super.setupRabbitMQ();

        declareConsume(SSL_DETECTION_BALLS_EXCHANGE, this::consume_SSL_DetectionBalls, null);
    }

    public void consume_SSL_DetectionBalls(Object object) {
        List<SSL_DetectionBall> sslDetectionBalls = (List<SSL_DetectionBall>) object;
        for (SSL_DetectionBall sslDetectionBall : sslDetectionBalls) {
            System.out.println(sslDetectionBall);
        }
    }
}