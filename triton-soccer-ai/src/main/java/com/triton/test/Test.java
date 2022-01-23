package com.triton.test;

public enum Test {
    KICK("Test the ability of robots to kick the ball."),
    DRIBBLE("Test the ability of robots to dribble the ball."),
    MATCH_VELOCITY("Test the ability of robots to match a target velocity."),
    MOVE_TO_POINT("Test the ability of robots to move to a point."),
    PATH_TO_POINT("Test the ability of robots to path to a point."),
    CHASE_BALL("Test the ability of robots to follow the ball."),
    CATCH_BALL("Test the ability of robots to catch the ball."),
    GOAL_KEEP("Test the ability of robots to keep the goal."),
    A_STAR_SEARCH("Test A*."),
    ;

    private final String desc;

    Test(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
