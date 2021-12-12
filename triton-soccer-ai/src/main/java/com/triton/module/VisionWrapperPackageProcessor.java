package com.triton.module;

import com.triton.Module;
import proto.vision.MessagesRobocupSslWrapper;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.triton.publisher_consumer.Exchange.SSL_WRAPPER_PACKAGE_EXCHANGE;

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
        MessagesRobocupSslWrapper.SSL_WrapperPacket sslWrapperPacket = (MessagesRobocupSslWrapper.SSL_WrapperPacket) object;
        System.out.println(sslWrapperPacket);
    }
}