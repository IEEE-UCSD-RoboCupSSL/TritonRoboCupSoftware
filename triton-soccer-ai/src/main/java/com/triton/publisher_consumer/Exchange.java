package com.triton.publisher_consumer;

public enum Exchange {
    SSL_WRAPPER_PACKAGE_EXCHANGE("SSL_WrapperPacket"),
    SSL_GEOMETRY_DATA_EXCHANGE("SSL_GeometryData"),
    SSL_DETECTION_FRAME_EXCHANGE("SSL_DetectionFrame"),
    SSL_DETECTION_BALLS_EXCHANGE("SSL_DetectionBall"),
    SSL_DETECTION_ROBOTS_YELLOW_EXCHANGE("SSL_DetectionRobot Yellow"),
    SSL_DETECTION_ROBOTS_BLUE_EXCHANGE("SSL_DetectionRobot Blue");

    private final String exchangeName;

    Exchange(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getExchangeName() {
        return exchangeName;
    }
}