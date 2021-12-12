package com.triton.module;

import com.triton.Module;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_DETECTION_BALLS_EXCHANGE;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;

public class VisionDetectionBallsProcessor extends Module {

    public VisionDetectionBallsProcessor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) {
        try {
            new VisionDetectionBallsProcessor();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(SSL_DETECTION_BALLS_EXCHANGE, this::consume_SSL_DetectionBalls);
    }

    private void consume_SSL_DetectionBalls(Object object) {
        List<SSL_DetectionBall> sslDetectionBalls = (List<SSL_DetectionBall>) object;
        for (SSL_DetectionBall sslDetectionBall : sslDetectionBalls) {
            System.out.println(sslDetectionBall);
        }
    }
}