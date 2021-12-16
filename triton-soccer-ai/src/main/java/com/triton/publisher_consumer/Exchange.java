package com.triton.publisher_consumer;

import proto.vision.MessagesRobocupSslGeometry;
import proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

import java.util.List;

import static proto.vision.MessagesRobocupSslGeometry.*;

public enum Exchange {
    RAW_WRAPPER_PACKAGE,
    BIASED_FIELD,
    BIASED_BALLS,
    BIASED_ALLIES,
    BIASED_FOES,
}