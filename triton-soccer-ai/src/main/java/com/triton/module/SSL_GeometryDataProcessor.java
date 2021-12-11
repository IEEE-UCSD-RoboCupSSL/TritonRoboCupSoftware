package com.triton.module;

import com.triton.Module;
import proto.vision.MessagesRobocupSslGeometry;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.PublisherConsumer.Exchange.SSL_GEOMETRY_DATA_EXCHANGE;

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
    public void setupRabbitMQ() throws IOException, TimeoutException {
        super.setupRabbitMQ();

        declareConsume(SSL_GEOMETRY_DATA_EXCHANGE, this::consume_SSL_GeometryData);
    }

    public void consume_SSL_GeometryData(Object object) {
        MessagesRobocupSslGeometry.SSL_GeometryData sslGeometryData = (MessagesRobocupSslGeometry.SSL_GeometryData) object;
        System.out.println(sslGeometryData);
    }
}