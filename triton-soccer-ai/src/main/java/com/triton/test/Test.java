package com.triton.test;

public enum Test {
    FOLLOW_BALL("Test the ability of robots to follow the ball"),
    ;

    private final String desc;

    Test(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
