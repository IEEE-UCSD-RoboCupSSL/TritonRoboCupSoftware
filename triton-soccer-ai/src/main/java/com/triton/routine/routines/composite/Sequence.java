package com.triton.routine.routines.composite;

import com.triton.module.ally_module.AllyModule;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Context;

import java.util.List;

public class Sequence extends Composite {
    public Sequence(List<Routine> routines) {
        super(routines);
    }

    @Override
    public void act(AllyModule allyModule, Context context) {
        for (Routine routine : routines) {
            routine.act(allyModule, context);
            if (routine.isFailure()) {
                fail();
                return;
            }
        }
        succeed();
    }
}
