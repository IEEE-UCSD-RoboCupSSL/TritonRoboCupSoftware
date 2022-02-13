package com.triton.routine.routines.decorator;

import com.triton.routine.base.Routine;

public abstract class Decorator extends Routine {
    protected final Routine routine;

    public Decorator(Routine routine) {
        this.routine = routine;
    }

    @Override
    public void reset() {
        routine.reset();
    }
}
