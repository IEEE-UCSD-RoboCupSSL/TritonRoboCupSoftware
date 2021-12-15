package com.triton.module;

import proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.RAW_DETECTION;

public class VisionDetectionFrameProcessor extends Module {

    public VisionDetectionFrameProcessor() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(RAW_DETECTION, this::consume_SSL_DetectionFrame);
    }

    private void consume_SSL_DetectionFrame(Object object) {
        SSL_DetectionFrame sslDetectionFrame = (SSL_DetectionFrame) object;
        System.out.println(sslDetectionFrame);
    }
}