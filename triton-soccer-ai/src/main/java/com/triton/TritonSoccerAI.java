package com.triton;

import com.triton.ai.Team;
import com.triton.module.interface_module.*;
import com.triton.module.processing_module.RobotControlCreator;
import com.triton.module.processing_module.VisionProcessor;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TritonSoccerAI {
    private static Team team;

    public TritonSoccerAI() throws IOException, TimeoutException {
        // processors
        new VisionProcessor().start();
//        new SimulatorCommandCreator().start();
        new RobotControlCreator().start();

        // interfaces
        new CameraInterface().start();
        new SimulatorCommandInterface().start();
        new SimulatorRobotControlInterface().start();
        new TritonBotCommandInterface().start();
        new UserInterface().start();
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
