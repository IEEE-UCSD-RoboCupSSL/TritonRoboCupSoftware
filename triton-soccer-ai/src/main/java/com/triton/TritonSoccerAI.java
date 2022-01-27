package com.triton;

import com.triton.config.*;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.constant.Test;
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
import com.triton.module.test_module.coordinated_skill_test.PathToFormationTest;
import com.triton.module.test_module.individual_skill_test.*;
import com.triton.module.test_module.misc_test.AStarSearchTest;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static com.triton.config.ConfigPath.*;
import static com.triton.config.ConfigReader.readConfig;

public class TritonSoccerAI {
    private final ScheduledThreadPoolExecutor executor;
    private final List<Module> modules;
    private final List<Future<?>> futures;

    public TritonSoccerAI() {
        super();
        modules = new ArrayList<>();
        futures = new ArrayList<>();
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1000);
        executor.setRemoveOnCancelPolicy(true);
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
        List<Future<?>> testFutures = new ArrayList<>();
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
                case KICK -> startModule(new KickTest(executor), testRunners, testFutures);
                case DRIBBLE -> startModule(new DribbleTest(executor), testRunners, testFutures);
                case MATCH_VELOCITY -> startModule(new MatchVelocityTest(executor), testRunners, testFutures);
                case MOVE_TO_POINT -> startModule(new MoveToPointTest(executor), testRunners, testFutures);
                case PATH_TO_POINT -> startModule(new PathToPointTest(executor), testRunners, testFutures);
                case CHASE_BALL -> startModule(new ChaseBallTest(executor), testRunners, testFutures);
                case CATCH_BALL -> startModule(new CatchBallTest(executor), testRunners, testFutures);
                case GOAL_KEEP -> startModule(new GoalKeepTest(executor), testRunners, testFutures);
                case DRIBBLE_BALL -> startModule(new DribbleBallTest(executor), testRunners, testFutures);
                case PATH_TO_FORMATION -> startModule(new PathToFormationTest(executor), testRunners, testFutures);
                case A_STAR_SEARCH -> startModule(new AStarSearchTest(executor), testRunners, testFutures);
                default -> System.out.println("Test not found.");
            }

            while (!testRunners.isEmpty()) {
                System.out.print("Running test, type 'q' to stop:\t");
                if (scanner.nextLine().equals("q")) {
                    testFutures.forEach(testFuture -> testFuture.cancel(false));
                    testRunners.forEach(Module::interrupt);
                    testRunners.clear();
                    testFutures.clear();
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
        startModule(new VisionBiasedConverter(executor), modules, futures);
        startModule(new FilterModule(executor), modules, futures);
        startModule(new SimulatorControlAudienceConverter(executor), modules, futures);
        startModule(new RobotCommandAudienceConverter(executor), modules, futures);
        startModule(new TritonBotMessageBuilder(executor), modules, futures);
    }

    public void startAI() {
        // core ai modules
        startModule(new AIModule(executor), modules, futures);
    }

    public void startInterfaceModules() {
        startModule(new CameraInterface(executor), modules, futures);
        startModule(new SimulatorCommandInterface(executor), modules, futures);
//        startModule(new SimulatorRobotCommandInterface(executor), modules, futures);
        startModule(new TritonBotMessageInterface(executor), modules, futures);
        startModule(new UserInterface(executor), modules, futures);
    }

    private Test parseTest(String line) {
        for (Test test : Test.values()) {
            if (line.equals(test.name()) || line.equals(String.valueOf(test.ordinal()))) {
                return test;
            }
        }
        return null;
    }

    public void startModule(Module module, List<Module> modules, List<Future<?>> futures) {
        Future<?> future = executor.submit(module);
        modules.add(module);
        futures.add(future);
    }
}
