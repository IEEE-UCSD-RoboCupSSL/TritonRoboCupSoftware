package com.triton;

import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.ai_module.*;
import com.triton.module.debug_module.FollowBall;
import com.triton.module.debug_module.SimulatorControlSource;
import com.triton.module.interface_module.CameraInterface;
import com.triton.module.interface_module.SimulatorCommandInterface;
import com.triton.module.interface_module.TritonBotMessageInterface;
import com.triton.module.interface_module.UserInterface;
import com.triton.module.processing_module.RobotCommandAudienceConverter;
import com.triton.module.processing_module.SimulatorControlAudienceConverter;
import com.triton.module.processing_module.TritonBotMessageBuilder;
import com.triton.module.processing_module.VisionBiasedConverter;
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
        // DEBUG MODULES
        new SimulatorControlSource().start();
//        new SimulatorConfigSource().start();
//        new RobotCommandSource().start();

        // PROCESSING MODULES
        new VisionBiasedConverter().start();
        new SimulatorControlAudienceConverter().start();
        new RobotCommandAudienceConverter().start();
        new TritonBotMessageBuilder().start();

        // AI MODULES
        new FollowBall().start();
        new OverviewModule().start();
        new StrategyModule().start();
        new TeamSkillsModule().start();
        new CoordinatedSkillsModule().start();
        new IndividualSkillsModule().start();
        new BasicSkillsModule().start();

        // INTERFACE MODULE
        new CameraInterface().start();
        new SimulatorCommandInterface().start();
//        new SimulatorRobotCommandInterface().start();
        new TritonBotMessageInterface().start();
        new UserInterface().start();
    }
}
