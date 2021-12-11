package com.triton.module;

import com.triton.PublisherConsumer.Module;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.PublisherConsumer.Exchange.SSL_DETECTION_FRAME_EXCHANGE;

public class SSL_DetectionFrameProcessor extends Module {

    public SSL_DetectionFrameProcessor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) {
        try {
            new SSL_DetectionFrameProcessor();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupRabbitMQ() throws IOException, TimeoutException {
        super.setupRabbitMQ();

        declareConsume(SSL_DETECTION_FRAME_EXCHANGE, this::consume_SSL_DetectionFrame, null);
    }

    public void consume_SSL_DetectionFrame(Object object) {
        MessagesRobocupSslDetection.SSL_DetectionFrame sslDetectionFrame = (MessagesRobocupSslDetection.SSL_DetectionFrame) object;
        System.out.println(sslDetectionFrame);
    }
}