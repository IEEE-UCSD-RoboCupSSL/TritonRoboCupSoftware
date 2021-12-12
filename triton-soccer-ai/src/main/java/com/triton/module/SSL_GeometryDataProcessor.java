package com.triton.module;

import com.triton.Module;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_GEOMETRY_DATA_EXCHANGE;

public class SSL_GeometryDataProcessor extends Module {

    public SSL_GeometryDataProcessor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) {
        try {
            new SSL_GeometryDataProcessor();
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
        MessagesRobocupSslGeometry.SSL_GeometryData sslGeometryData = (MessagesRobocupSslGeometry.SSL_GeometryData) object;
        System.out.println(sslGeometryData);
    }
}