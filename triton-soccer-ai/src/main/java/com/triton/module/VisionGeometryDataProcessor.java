package com.triton.module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.RAW_GEOMETRY;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData;

public class VisionGeometryDataProcessor extends Module {

    public VisionGeometryDataProcessor() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(RAW_GEOMETRY, this::consume_SSL_GeometryData);
    }

    private void consume_SSL_GeometryData(Object object) {
        SSL_GeometryData sslGeometryData = (SSL_GeometryData) object;
        System.out.println(sslGeometryData);
    }
}