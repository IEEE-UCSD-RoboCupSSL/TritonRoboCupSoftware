package com.triton;

import com.triton.config.*;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.module.Module;
import com.triton.module.ai_module.core.*;
import com.triton.module.ai_module.helper.PathfindingModule;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import static com.triton.config.ConfigPath.*;
import static com.triton.config.ConfigReader.readConfig;

public class TritonSoccerAI {
    ThreadPoolExecutor executor;

    public TritonSoccerAI() {
        super();
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
    }

    public static void main(String[] args) {
        if (parseArgs(args)) return;
        loadConfigs();

        try {
            TritonSoccerAI tritonSoccerAI = new TritonSoccerAI();
            tritonSoccerAI.startModules();
            if (RuntimeConstants.test)
                tritonSoccerAI.runTests();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
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

    public void startModules() throws IOException, TimeoutException {
        startProcessingModules();
        startAI();
        startInterfaceModules();
    }

    public void startProcessingModules() throws IOException, TimeoutException {
        startModule(new VisionBiasedConverter());
        startModule(new FilterModule());
        startModule(new SimulatorControlAudienceConverter());
        startModule(new RobotCommandAudienceConverter());
        startModule(new TritonBotMessageBuilder());
    }

    public void startAI() throws IOException, TimeoutException {
        // helper ai modules
        startModule(new PathfindingModule());

        // core ai modules
        startModule(new OverviewModule());
        startModule(new StrategyModule());
        startModule(new TeamSkillsModule());
        startModule(new CoordinatedSkillsModule());
        startModule(new IndividualSkillsModule());
        startModule(new BasicSkillsModule());
    }

    public void startInterfaceModules() throws IOException, TimeoutException {
        startModule(new CameraInterface());
        startModule(new SimulatorCommandInterface());
//        startModule(new SimulatorRobotCommandInterface());
        startModule(new TritonBotMessageInterface());
        startModule(new UserInterface());
    }

    private void runTests() throws IOException, TimeoutException {
        ArrayList<Module> testModules = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Available tests:");
            for (Test test : Test.values())
                System.out.println("- " + test.ordinal() + ". " + test.name() + ":\n\t\t" + test.getDesc());

            System.out.print("Choose a test:\t");
            Test test = parseTest(scanner.nextLine());

            switch (test) {
                case KICK -> startModule(new KickTest(), testModules);
                case DRIBBLE -> startModule(new DribbleTest(), testModules);
                case MATCH_VELOCITY -> startModule(new MatchVelocityTest(), testModules);
                case MOVE_TO_POINT -> startModule(new MoveToPointTest(), testModules);
                case PATH_TO_POINT -> startModule(new PathToPointTest(), testModules);
                case CHASE_BALL -> startModule(new ChaseBallTest(), testModules);
                case CATCH_BALL -> startModule(new CatchBallTest(), testModules);
                case GOAL_KEEP -> startModule(new GoalKeepTest(), testModules);
                case DRIBBLE_BALL -> startModule(new DribbleBallTest(), testModules);
                case A_STAR_SEARCH -> startModule(new AStarSearchTest(), testModules);
                default -> System.out.println("Test not found.");
            }

            while (!testModules.isEmpty()) {
                System.out.print("Running test, type 'q' to stop:\t");
                if (scanner.nextLine().equals("q")) {
                    for (Module module : testModules) {
                        executor.remove(module);
                        module.interrupt();
                    }
                    testModules.clear();
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

    public void startModule(Module module) {
        executor.execute(module);
    }

    public void startModule(Module module, ArrayList<Module> modules) {
        executor.execute(module);
        modules.add(module);
    }
}
