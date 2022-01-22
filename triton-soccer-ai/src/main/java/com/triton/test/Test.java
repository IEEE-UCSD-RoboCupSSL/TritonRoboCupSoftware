package com.triton.test;

public enum Test {
    KICK("Test the ability of robots to kick the ball."),
    DRIBBLE("Test the ability of robots to dribble the ball."),
    MATCH_VELOCITY("Test the ability of robots to match a target velocity"),
    CHASE_BALL("Test the ability of robots to follow the ball."),
    ;

    private final String desc;

    Test(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
