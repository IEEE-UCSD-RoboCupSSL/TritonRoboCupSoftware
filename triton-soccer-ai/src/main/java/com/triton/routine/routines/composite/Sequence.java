package com.triton.routine.routines.composite;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;

import java.util.List;

public class Sequence extends Composite {
    public Sequence(List<Routine> routines) {
        super(routines);
    }

    @Override
    public void act(Runner runner, Context context) {
        for (Routine routine : routines) {
            routine.act(runner, context);
            if (routine.isFailure()) {
                fail();
                return;
            }
        }
        succeed();
    }
}
