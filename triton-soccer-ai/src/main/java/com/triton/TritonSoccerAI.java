package com.triton;

import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.interface_module.CameraInterface;
import com.triton.module.interface_module.SimulatorCommandInterface;
import com.triton.module.interface_module.TritonBotCommandInterface;
import com.triton.module.interface_module.UserInterface;
import com.triton.module.processing_module.RobotControlCreator;
import com.triton.module.processing_module.VisionProcessor;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TritonSoccerAI {
    public TritonSoccerAI() {
        super();

        try {
            startModules();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Options options = new Options();
        Option teamOption = Option.builder("t")
                .longOpt("team")
                .argName("team")
                .hasArg()
                .required(true)
                .desc("set team to manage").build();
        options.addOption(teamOption);

        CommandLine cmd;
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helper = new HelpFormatter();

        Team team = null;
        try {
            cmd = parser.parse(options, args);
            if (cmd.hasOption(teamOption))
                team = parseTeam(cmd.getOptionValue(teamOption));
        } catch (ParseException e) {
            e.printStackTrace();
            helper.printHelp(" ", options);
            return;
        }

        if (team == null) {
            throw new IllegalStateException();
        }

        RuntimeConstants.team = team;

        new TritonSoccerAI();
    }

    private static Team parseTeam(String teamString) {
        for (Team team : Team.values())
            if (teamString.equals(team.getTeamString()))
                return team;
        return null;
    }

    private void startModules() throws IOException, TimeoutException {
        // processors
        new VisionProcessor().start();
//        new SimulatorCommandCreator().start();
        new RobotControlCreator().start();

        // interfaces
        new CameraInterface().start();
        new SimulatorCommandInterface().start();
//        new SimulatorRobotControlInterface().start();
        new TritonBotCommandInterface().start();
        new UserInterface().start();
    }
}
