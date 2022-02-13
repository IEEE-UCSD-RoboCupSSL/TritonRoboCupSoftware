package com.triton.routine.routines.decorator;

import com.triton.module.ally_module.AllyModule;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Context;

public class Inverter extends Decorator {
    public Inverter(Routine routine) {
        super(routine);
    }

    @Override
    public void act(AllyModule allyModule, Context context) {
        routine.act(allyModule, context);
        if (routine.isFailure())
            succeed();
        else
            fail();
    }
}
