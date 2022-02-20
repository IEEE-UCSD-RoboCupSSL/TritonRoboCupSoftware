package com.triton.routine.routines.composite;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;

import java.util.List;

public class Selector extends Composite {
    public Selector(List<Routine> routines) {
        super(routines);
    }

    @Override
    public void act(Runner runner, Context context) {
        for (Routine routine : routines) {
            routine.act(runner, context);
            if (routine.isSuccess()) {
                succeed();
                return;
            }
        }
        fail();
    }
}
