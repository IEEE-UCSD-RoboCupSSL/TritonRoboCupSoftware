package com.triton.constant;

public enum Team {
    YELLOW("yellow"),
    BLUE("blue"),
    ;

    private final String teamString;

    Team(String teanString) {
        this.teamString = teanString;
    }

    public String getTeamString() {
        return teamString;
    }
}
