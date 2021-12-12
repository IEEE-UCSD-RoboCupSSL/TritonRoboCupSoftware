package com.triton.module;

import com.triton.Module;
import proto.vision.MessagesRobocupSslDetection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_DETECTION_FRAME_EXCHANGE;

public class VisionDetectionFrameProcessor extends Module {

    public VisionDetectionFrameProcessor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) {
        try {
            new VisionDetectionFrameProcessor();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();

        declareConsume(SSL_DETECTION_FRAME_EXCHANGE, this::consume_SSL_DetectionFrame);
    }

    private void consume_SSL_DetectionFrame(Object object) {
        MessagesRobocupSslDetection.SSL_DetectionFrame sslDetectionFrame = (MessagesRobocupSslDetection.SSL_DetectionFrame) object;
        System.out.println(sslDetectionFrame);
    }
}