package com.triton.constant;

import com.triton.module.TestRunner;
import com.triton.module.old_test_module.basic_skill_test.DribbleTest;
import com.triton.module.old_test_module.basic_skill_test.KickTest;
import com.triton.module.old_test_module.basic_skill_test.MatchVelocityTest;
import com.triton.module.old_test_module.coordinated_skill_test.PassTest;
import com.triton.module.old_test_module.coordinated_skill_test.PathToFormationTest;
import com.triton.module.old_test_module.individual_skill_test.*;
import com.triton.module.old_test_module.tandem_skill_test.TandemAttackAndDefendPrimaryTest;
import com.triton.module.old_test_module.tandem_skill_test.TandemShootAndKeepPrimaryTest;
import com.triton.module.old_test_module.tandem_skill_test.TandemShootAndKeepSecondaryTest;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public enum Test {
    KICK(KickTest.class, "Test the ability of robots to kick the ball."),
    DRIBBLE(DribbleTest.class, "Test the ability of robots to dribble the ball."),
    MATCH_VELOCITY(MatchVelocityTest.class, "Test the ability of robots to match a target velocity."),
    MOVE_TO_TARGET(MoveToTargetTest.class, "Test the ability of robots to match a position."),
    PATH_TO_TARGET(PathToTargetTest.class, "Test the ability of robots to path to a point."),
    CHASE_BALL(ChaseBallTest.class, "Test the ability of robots to follow the ball."),
    CATCH_BALL(CatchBallTest.class, "Test the ability of robots to catch the ball."),
    GOAL_KEEP(GoalKeepTest.class, "Test the ability of robots to keep the goal."),
    KICK_TOWARD_TARGET(KickTowardTargetTest.class, "Test the ability of robots to kick the ball toward a specific point."),
    KICK_FROM_POSITION(KickFromPositionTest.class, "Test the ability of robots to kick the ball toward a target from " +
            "a specific position."),
    GOAL_SHOOT(GoalShootTest.class, "Test the ability of robots to kick the ball toward the goal"),
    DRIBBLE_BALL(DribbleBallTest.class, "Test the ability of robots to dribble the ball to a specific position."),
    PATH_TO_FORMATION(PathToFormationTest.class, "Test the ability of robots to arrange themselves in a formation."),
    PASS(PassTest.class, "Test the ability of robots to pass the ball between themselves."),
    TANDEM_SHOOT_AND_KEEP_PRIMARY(TandemShootAndKeepPrimaryTest.class, "Test the ability of robots to shoot against a " +
            "goalkeeper"),
    TANDEM_SHOOT_AND_KEEP_SECONDARY(TandemShootAndKeepSecondaryTest.class, "Test the ability of robots to defend against a " +
            "shooter"),
    TANDEM_ATTACK_AND_DEFEND_PRIMARY(TandemAttackAndDefendPrimaryTest.class, "Test the ability of robots to attack"),
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
