package com.triton;

import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.interface_module.*;
import com.triton.module.processing_module.*;
import com.triton.module.source_module.FollowBall;
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
        // SOURCE MODULES
//        new SimulatorControlSource().start();
//        new SimulatorConfigSource().start();
//        new RobotCommandSource().start();
        new FollowBall().start();

        // PROCESSING MODULES
        // incoming
        new VisionBiasedConverter().start();
        // outgoing
        new SimulatorControlAudienceConverter().start();
        new RobotCommandAudienceConverter().start();
        new TritonBotMessageBuilder().start();

        // INTERFACE MODULE
        new CameraInterface().start();
        new SimulatorCommandInterface().start();
//        new SimulatorRobotCommandInterface().start();
        new TritonBotMessageInterface().start();
        new UserInterface().start();
    }
}
