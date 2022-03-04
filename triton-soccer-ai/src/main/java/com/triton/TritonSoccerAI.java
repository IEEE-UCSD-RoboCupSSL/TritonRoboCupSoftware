package com.triton;

import com.triton.config.*;
import com.triton.constant.AITest;
import com.triton.constant.ProgramConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import com.triton.module.TestModule;
import com.triton.module.ai_module.AIModule;
import com.triton.module.interface_module.CameraInterface;
import com.triton.module.interface_module.SimulatorCommandInterface;
import com.triton.module.interface_module.TritonBotMessageInterface;
import com.triton.module.interface_module.UserInterface;
import com.triton.module.processing_module.*;
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
        tritonSoccerAI.startSupportModules();
        if (ProgramConstants.test) {
            tritonSoccerAI.runTests();
        } else {
            tritonSoccerAI.startAI();
        }
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
            if (cmd.hasOption(teamOption)) {
                if (cmd.getOptionValue(teamOption).equals(Team.YELLOW.getTeamString())) {
                    ProgramConstants.team = Team.YELLOW;
                    ProgramConstants.foeTeam = Team.BLUE;
                } else {
                    ProgramConstants.team = Team.BLUE;
                    ProgramConstants.foeTeam = Team.YELLOW;
                }
            }
            if (cmd.hasOption(testOption)) {
                ProgramConstants.test = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            helper.printHelp(" ", options);
            return true;
        }
        return false;
    }

    private static void loadConfigs() {
        ProgramConstants.aiConfig = (AIConfig) readConfig(AI_CONFIG);
        ProgramConstants.displayConfig = (DisplayConfig) readConfig(DISPLAY_CONFIG);
        ProgramConstants.gameConfig = (GameConfig) readConfig(GAME_CONFIG);
        ProgramConstants.networkConfig = (NetworkConfig) readConfig(NETWORK_CONFIG);
        ProgramConstants.objectConfig = (ObjectConfig) readConfig(OBJECT_CONFIG);
    }

    public void startSupportModules() {
        startProcessingModules();
        startInterfaceModules();
    }

    private void runTests() {
        List<Module> testModules = new ArrayList<>();
        List<Future<?>> testFutures = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Available tests:");
            for (AITest test : AITest.values())
                System.out.println("- " + test.ordinal() + ". " + test.name() + ":\n\t" + test.getDesc());

            System.out.print("Choose a test:\t");
            AITest test = parseTest(scanner.nextLine());

            if (test == null) {
                System.out.println("Test not found. Try again.");
                continue;
            }

            TestModule testModule = test.createNewTestModule(executor);
            startModule(testModule, testModules, testFutures);

            while (!testModules.isEmpty()) {
                System.out.print("Running test, type 'q' to stop:\t");
                if (scanner.nextLine().equals("q")) {
                    testFutures.forEach(testFuture -> testFuture.cancel(false));
                    testModules.forEach(Module::interrupt);
                    testModules.clear();
                    testFutures.clear();
                }
            }
        }
    }

    public void startAI() {
        // core ai modules
        startModule(new AIModule(executor), modules, futures);
    }

    public void startProcessingModules() {
        startModule(new VisionBiasedConverter(executor), modules, futures);
        startModule(new FilterModule(executor), modules, futures);
        startModule(new SimulatorControlAudienceConverter(executor), modules, futures);
        startModule(new RobotCommandAudienceConverter(executor), modules, futures);
        startModule(new TritonBotMessageBuilder(executor), modules, futures);
    }

    public void startInterfaceModules() {
        startModule(new CameraInterface(executor), modules, futures);
        startModule(new SimulatorCommandInterface(executor), modules, futures);
//        startModule(new SimulatorRobotCommandInterface(executor), modules, futures);
        startModule(new TritonBotMessageInterface(executor), modules, futures);
        startModule(new UserInterface(executor), modules, futures);
    }

    private AITest parseTest(String line) {
        for (AITest test : AITest.values())
            if (line.equals(test.name()) || line.equals(String.valueOf(test.ordinal())))
                return test;
        return null;
    }

    public void startModule(Module module, List<Module> modules, List<Future<?>> futures) {
        Future<?> future = executor.submit(module);
        modules.add(module);
        futures.add(future);
    }
}
