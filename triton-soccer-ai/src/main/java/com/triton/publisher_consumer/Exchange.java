package com.triton.publisher_consumer;

import proto.vision.MessagesRobocupSslGeometry;
import proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

import java.util.List;

import static proto.vision.MessagesRobocupSslGeometry.*;

public enum Exchange {
    RAW_WRAPPER_PACKAGE("RAW_WRAPPER_PACKAGE"),
    RAW_GEOMETRY("RAW_GEOMETRY"),
    RAW_DETECTION("RAW_DETECTION"),
    RAW_BALLS("RAW_BALLS"),
    RAW_ROBOTS_YELLOW("RAW_ROBOTS_YELLOW"),
    RAW_ROBOTS_BLUE("RAW_ROBOTS_BLUE"),
    PERSPECTIVE_FIELD("PERSPECTIVE_GEOMETRY"),
    PERSPECTIVE_BALLS("PERSPECTIVE_BALLS"),
    PERSPECTIVE_ROBOTS_ALLY("PERSPECTIVE_ROBOTS_ALLY"),
    PERSPECTIVE_ROBOTS_FOE("PERSPECTIVE_ROBOTS_FOE"),
    ;

    private final String exchangeName;

    Exchange(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getExchangeName() {
        return exchangeName;
    }
}