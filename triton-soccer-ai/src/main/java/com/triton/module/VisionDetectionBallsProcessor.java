package com.triton.module;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.RAW_BALLS;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;

public class VisionDetectionBallsProcessor extends Module {

    public VisionDetectionBallsProcessor() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(RAW_BALLS, this::consume_SSL_DetectionBalls);
    }

    private void consume_SSL_DetectionBalls(Object object) {
        List<SSL_DetectionBall> sslDetectionBalls = (List<SSL_DetectionBall>) object;
        for (SSL_DetectionBall sslDetectionBall : sslDetectionBalls) {
            System.out.println(sslDetectionBall);
        }
    }
}