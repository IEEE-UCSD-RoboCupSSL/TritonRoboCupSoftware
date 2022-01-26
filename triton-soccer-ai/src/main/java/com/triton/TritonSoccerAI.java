package com.triton;

import com.triton.config.*;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import com.triton.module.ai_module.AIModule;
import com.triton.module.interface_module.CameraInterface;
import com.triton.module.interface_module.SimulatorCommandInterface;
import com.triton.module.interface_module.TritonBotMessageInterface;
import com.triton.module.interface_module.UserInterface;
import com.triton.module.processing_module.*;
import com.triton.module.test_module.basic_skill_test.DribbleTest;
import com.triton.module.test_module.basic_skill_test.KickTest;
import com.triton.module.test_module.basic_skill_test.MatchVelocityTest;
import com.triton.module.test_module.basic_skill_test.MoveToPointTest;
import com.triton.module.test_module.individual_skill_test.*;
import com.triton.module.test_module.misc_test.AStarSearchTest;
import com.triton.util.Test;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.triton.config.ConfigPath.*;
import static com.triton.config.ConfigReader.readConfig;

public class TritonSoccerAI {
    private final ScheduledThreadPoolExecutor executor;
    private final List<Module> modules;

    public TritonSoccerAI() {
        super();
        modules = new ArrayList<>();
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(100);
    }

    public static void main(String[] args) {
        if (parseArgs(args)) return;
        loadConfigs();

        TritonSoccerAI tritonSoccerAI = new TritonSoccerAI();
        tritonSoccerAI.startModules();
        if (RuntimeConstants.test)
            tritonSoccerAI.runTests();
    }

    private static boolean parseArgs(String[] args) {
        Options options = new Options();
        Option teamOption = Option.builder("team")
                .longOpt("team")
                .argName("team")
                .hasArg()
                .required(true)
                .desc("set team to manage").build();
        options.addOption(teamOption);

        Option testOption = Option.builder("test")
                .longOpt("test")
                .argName("test")
                .required(false)
                .desc("whether to run in test mode").build();
        options.addOption(testOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption(teamOption))
                RuntimeConstants.team = parseTeam(cmd.getOptionValue(teamOption));
            if (cmd.hasOption(testOption)) {
                RuntimeConstants.test = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            helper.printHelp(" ", options);
            return true;
        }
        return false;
    }

    private static void loadConfigs() {
        RuntimeConstants.aiConfig = (AIConfig) readConfig(AI_CONFIG);
        RuntimeConstants.displayConfig = (DisplayConfig) readConfig(DISPLAY_CONFIG);
        RuntimeConstants.gameConfig = (GameConfig) readConfig(GAME_CONFIG);
        RuntimeConstants.networkConfig = (NetworkConfig) readConfig(NETWORK_CONFIG);
        RuntimeConstants.objectConfig = (ObjectConfig) readConfig(OBJECT_CONFIG);
    }

    public void startModules() {
        startProcessingModules();
        startAI();
        startInterfaceModules();
    }

    private void runTests() {
        List<Module> testRunners = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Available tests:");
            for (Test test : Test.values())
                System.out.println("- " + test.ordinal() + ". " + test.name() + ":\n\t\t" + test.getDesc());

            System.out.print("Choose a test:\t");
            Test test = parseTest(scanner.nextLine());

            if (test == null) {
                System.out.println("Test not found. Try again.");
                continue;
            }

            switch (test) {
                case KICK -> startModule(new KickTest(executor), testRunners);
                case DRIBBLE -> startModule(new DribbleTest(executor), testRunners);
                case MATCH_VELOCITY -> startModule(new MatchVelocityTest(executor), testRunners);
                case MOVE_TO_POINT -> startModule(new MoveToPointTest(executor), testRunners);
                case PATH_TO_POINT -> startModule(new PathToPointTest(executor), testRunners);
                case CHASE_BALL -> startModule(new ChaseBallTest(executor), testRunners);
                case CATCH_BALL -> startModule(new CatchBallTest(executor), testRunners);
                case GOAL_KEEP -> startModule(new GoalKeepTest(executor), testRunners);
                case DRIBBLE_BALL -> startModule(new DribbleBallTest(executor), testRunners);
                case A_STAR_SEARCH -> startModule(new AStarSearchTest(executor), testRunners);
                default -> System.out.println("Test not found.");
            }

            while (!testRunners.isEmpty()) {
                System.out.print("Running test, type 'q' to stop:\t");
                if (scanner.nextLine().equals("q")) {
                    testRunners.forEach(testRunner -> {
                        testRunner.interrupt();
                    });
                    testRunners.clear();
                }
            }
        }
    }

    private static Team parseTeam(String teamString) {
        for (Team team : Team.values())
            if (teamString.equals(team.getTeamString()))
                return team;
        return Team.YELLOW;
    }

    public void startProcessingModules() {
        startModule(new VisionBiasedConverter(executor), modules);
        startModule(new FilterModule(executor), modules);
        startModule(new SimulatorControlAudienceConverter(executor), modules);
        startModule(new RobotCommandAudienceConverter(executor), modules);
        startModule(new TritonBotMessageBuilder(executor), modules);
    }

    public void startAI() {
        // core ai modules
        startModule(new AIModule(executor), modules);
    }

    public void startInterfaceModules() {
        startModule(new CameraInterface(executor), modules);
        startModule(new SimulatorCommandInterface(executor), modules);
//        startModule(new SimulatorRobotCommandInterface(executor), modules);
        startModule(new TritonBotMessageInterface(executor), modules);
        startModule(new UserInterface(executor), modules);
    }

    private Test parseTest(String line) {
        for (Test test : Test.values()) {
            if (line.equals(test.name()) || line.equals(String.valueOf(test.ordinal()))) {
                return test;
            }
        }
        return null;
    }

    public void startModule(Module module, List<Module> modules) {
        executor.submit(module);
        modules.add(module);
    }
}
