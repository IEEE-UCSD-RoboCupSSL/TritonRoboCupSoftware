package com.triton.module;

import com.triton.Module;
import proto.vision.MessagesRobocupSslWrapper;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_WRAPPER_PACKAGE_EXCHANGE;

public class SSL_WrapperPackageProcessor extends Module {

    public SSL_WrapperPackageProcessor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) {
        try {
            new SSL_WrapperPackageProcessor();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupRabbitMQ() throws IOException, TimeoutException {
        super.setupRabbitMQ();

        declareConsume(SSL_WRAPPER_PACKAGE_EXCHANGE, this::consume_SSL_WrapperPacket);
    }

    public void consume_SSL_WrapperPacket(Object object) {
        MessagesRobocupSslWrapper.SSL_WrapperPacket sslWrapperPacket = (MessagesRobocupSslWrapper.SSL_WrapperPacket) object;
        System.out.println(sslWrapperPacket);
    }
}