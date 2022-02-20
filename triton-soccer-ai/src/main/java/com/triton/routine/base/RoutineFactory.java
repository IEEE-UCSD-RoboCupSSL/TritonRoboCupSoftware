package com.triton.routine.base;

import com.triton.routine.routines.composite.Sequence;
import com.triton.routine.routines.leaf.action.Dribble;
import com.triton.routine.routines.leaf.action.Kick;
import com.triton.routine.routines.leaf.action.MatchVelocity;

import java.util.List;

public class RoutineFactory {
    private static Sequence sequence(List<Routine> routines) {
        return null;
    }

    private static Dribble dribble() {
        return new Dribble();
    }

    private static Kick kick() {
        return new Kick();
    }

    private static MatchVelocity matchVelocity() {
        return new MatchVelocity();
    }
}
