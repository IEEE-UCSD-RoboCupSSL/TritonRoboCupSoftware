package com.triton.routine.routines.decorator;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;

public class Inverter extends Decorator {
    public Inverter(Routine routine) {
        super(routine);
    }

    @Override
    public void act(Runner runner, Context context) {
        routine.act(runner, context);
        if (routine.isFailure())
            succeed();
        else
            fail();
    }
}
