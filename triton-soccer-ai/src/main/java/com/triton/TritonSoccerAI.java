package com.triton;

import com.triton.module.CameraReceiver;
import com.triton.module.Display;
import com.triton.module.PerspectiveConverter;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TritonSoccerAI {
    private static Team team;

    public TritonSoccerAI(Team team) throws IOException, TimeoutException {
        TritonSoccerAI.team = team;

        new CameraReceiver();
        new PerspectiveConverter();
        new Display();
    }

    public static void main(String[] args) {
        if (args.length < 1) return;
        String teamString = args[0];
        Team team;

        if (teamString.equals(Team.YELLOW.getTeamString()))
            team = Team.YELLOW;
        else if (teamString.equals(Team.BLUE.getTeamString()))
            team = Team.BLUE;
        else
            throw new IllegalStateException();

        try {
            new TritonSoccerAI(team);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static Team getTeam() {
        return team;
    }
}
