package com.triton.constant;

public enum Team {
    BLUE("blue"),
    YELLOW("yellow"),
    ;

    private final String teamString;

    Team(String teanString) {
        this.teamString = teanString;
    }

    public String getTeamString() {
        return teamString;
    }
}
