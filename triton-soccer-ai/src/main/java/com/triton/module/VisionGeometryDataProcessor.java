package com.triton.module;

import com.triton.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_GEOMETRY_DATA_EXCHANGE;
import static proto.vision.MessagesRobocupSslGeometry.SSL_GeometryData;

public class VisionGeometryDataProcessor extends Module {

    public VisionGeometryDataProcessor() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    public static void main(String[] args) {
        try {
            new VisionGeometryDataProcessor();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(SSL_GEOMETRY_DATA_EXCHANGE, this::consume_SSL_GeometryData);
    }

    private void consume_SSL_GeometryData(Object object) {
        SSL_GeometryData sslGeometryData = (SSL_GeometryData) object;
        System.out.println(sslGeometryData);
    }
}