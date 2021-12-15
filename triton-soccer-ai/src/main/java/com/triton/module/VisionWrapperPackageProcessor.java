package com.triton.module;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.RAW_WRAPPER_PACKAGE;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class VisionWrapperPackageProcessor extends Module {

    public VisionWrapperPackageProcessor() throws IOException, TimeoutException {
        super();
        declareExchanges();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(RAW_WRAPPER_PACKAGE, this::consume_SSL_WrapperPacket);
    }

    private void consume_SSL_WrapperPacket(Object object) {
        SSL_WrapperPacket sslWrapperPacket = (SSL_WrapperPacket) object;
        System.out.println(sslWrapperPacket);
    }
}