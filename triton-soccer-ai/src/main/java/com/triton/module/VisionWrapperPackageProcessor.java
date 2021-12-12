package com.triton.module;

import com.triton.Module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_WRAPPER_PACKAGE_EXCHANGE;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class VisionWrapperPackageProcessor extends Module {

    public VisionWrapperPackageProcessor() throws IOException, TimeoutException {
        super();
    }

    public static void main(String[] args) {
        try {
            new VisionWrapperPackageProcessor();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(SSL_WRAPPER_PACKAGE_EXCHANGE, this::consume_SSL_WrapperPacket);
    }

    private void consume_SSL_WrapperPacket(Object object) {
        SSL_WrapperPacket sslWrapperPacket = (SSL_WrapperPacket) object;
        System.out.println(sslWrapperPacket);
    }
}