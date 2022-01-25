package com.triton;

import com.triton.config.*;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import com.triton.module.SkillRunner;
import com.triton.module.TestRunner;
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
import com.triton.test.Test;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.triton.config.ConfigPath.*;
import static com.triton.config.ConfigReader.readConfig;

public class TritonSoccerAI {
    private final ArrayList<Module> modules;

    public TritonSoccerAI() {
        super();
        modules = new ArrayList<>();
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

    private static Team parseTeam(String teamString) {
        for (Team team : Team.values())
            if (teamString.equals(team.getTeamString()))
                return team;
        return Team.YELLOW;
    }

    public void startModules() {
        startProcessingModules();
        startAI();
        startInterfaceModules();
    }

    public void startProcessingModules() {
        startModule(new VisionBiasedConverter(), modules);
        startModule(new FilterModule(), modules);
        startModule(new SimulatorControlAudienceConverter(), modules);
        startModule(new RobotCommandAudienceConverter(), modules);
        startModule(new TritonBotMessageBuilder(), modules);
    }

    public void startAI() {
        // core ai modules
        startModule(new AIModule(), modules);
    }

    public void startInterfaceModules() {
        startModule(new CameraInterface(), modules);
        startModule(new SimulatorCommandInterface(), modules);
//        startModule(new SimulatorRobotCommandInterface(), modules);
        startModule(new TritonBotMessageInterface(), modules);
        startModule(new UserInterface(), modules);
    }

    private void runTests() {
        ArrayList<Module> testRunners = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Available tests:");
            for (Test test : Test.values())
                System.out.println("- " + test.ordinal() + ". " + test.name() + ":\n\t\t" + test.getDesc());

            System.out.print("Choose a test:\t");
            Test test = parseTest(scanner.nextLine());

            switch (test) {
                case KICK -> startModule(new KickTest(), testRunners);
                case DRIBBLE -> startModule(new DribbleTest(), testRunners);
                case MATCH_VELOCITY -> startModule(new MatchVelocityTest(), testRunners);
                case MOVE_TO_POINT -> startModule(new MoveToPointTest(), testRunners);
                case PATH_TO_POINT -> startModule(new PathToPointTest(), testRunners);
                case CHASE_BALL -> startModule(new ChaseBallTest(), testRunners);
                case CATCH_BALL -> startModule(new CatchBallTest(), testRunners);
                case GOAL_KEEP -> startModule(new GoalKeepTest(), testRunners);
                case DRIBBLE_BALL -> startModule(new DribbleBallTest(), testRunners);
                case A_STAR_SEARCH -> startModule(new AStarSearchTest(), testRunners);
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

    private Test parseTest(String line) {
        for (Test test : Test.values()) {
            if (line.equals(test.name()) || line.equals(String.valueOf(test.ordinal()))) {
                return test;
            }
        }
        return null;
    }

    public void startModule(Module module, ArrayList<Module> modules) {
        module.start();
        modules.add(module);
    }
}
