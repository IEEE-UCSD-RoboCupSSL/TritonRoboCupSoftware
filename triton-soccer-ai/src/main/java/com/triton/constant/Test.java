package com.triton.constant;

import com.triton.module.TestRunner;
import com.triton.module.test_module.basic_skill_test.DribbleTest;
import com.triton.module.test_module.basic_skill_test.KickTest;
import com.triton.module.test_module.basic_skill_test.MatchVelocityTest;
import com.triton.module.test_module.coordinated_skill_test.PathToFormationTest;
import com.triton.module.test_module.individual_skill_test.*;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public enum Test {
    KICK_TEST(KickTest.class, "Test the ability of robots to kick the ball."),
    DRIBBLE_TEST(DribbleTest.class, "Test the ability of robots to dribble the ball."),
    MATCH_VELOCITY_TEST(MatchVelocityTest.class, "Test the ability of robots to match a target velocity."),
    MOVE_TO_POINT_TEST(MoveToPointTest.class, "Test the ability of robots to match a position."),
    PATH_TO_POINT_TEST(PathToPointTest.class, "Test the ability of robots to path to a point."),
    CHASE_BALL_TEST(ChaseBallTest.class, "Test the ability of robots to follow the ball."),
    CATCH_BALL_TEST(CatchBallTest.class, "Test the ability of robots to catch the ball."),
    GOAL_KEEP_TEST(GoalKeepTest.class, "Test the ability of robots to keep the goal."),
    KICK_TO_POINT_TEST(KickToPointTest.class, "Test the ability of robots to kick the ball toward a specific point."),
    DRIBBLE_BALL_TEST(DribbleBallTest.class, "Test the ability of robots to dribble the ball to a specific position."),
    PATH_TO_FORMATION_TEST(PathToFormationTest.class, "Test the ability of robots to arrange themselves in a " +
            "formation."),
    ;

    private final Class<? extends TestRunner> testClass;
    private final String desc;

    Test(Class<? extends TestRunner> testClass, String desc) {
        this.testClass = testClass;
        this.desc = desc;
    }

    public Class<? extends TestRunner> getTestRunnerClass() {
        return testClass;
    }

    public String getDesc() {
        return desc;
    }

    public TestRunner createNewTestRunner(ScheduledThreadPoolExecutor executor) {
        try {
            return testClass.getConstructor(ScheduledThreadPoolExecutor.class).newInstance(executor);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }
}
