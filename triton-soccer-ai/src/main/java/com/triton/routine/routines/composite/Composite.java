package com.triton.routine.routines.composite;

import com.triton.routine.base.Routine;

import java.util.List;

public abstract class Composite extends Routine {
    protected final List<Routine> routines;

    public Composite(List<Routine> routines) {
        super();
        this.routines = routines;
    }

    @Override
    public void reset() {
        for (Routine routine : routines)
            routine.reset();
    }
}
