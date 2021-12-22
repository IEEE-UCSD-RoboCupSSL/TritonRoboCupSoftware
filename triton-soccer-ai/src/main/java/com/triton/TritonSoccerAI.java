package com.triton;

import com.triton.module.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TritonSoccerAI {
    private static Team team;

    public TritonSoccerAI() throws IOException, TimeoutException {
        // input
        new VisionReceiver().start();

        // processing
        new PerspectiveConverter().start();
//        new SimulatorCommandCreator().start();
        new RobotControlCreator().start();

        // output
        new SimulatorCommandSender().start();
        new RobotControlSender().start();
        new TritonBotCommandSender().start();
        new Display().start();
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
            TritonSoccerAI.setTeam(team);
            new TritonSoccerAI();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static Team getTeam() {
        return team;
    }

    public static void setTeam(Team team) {
        TritonSoccerAI.team = team;
    }
}
