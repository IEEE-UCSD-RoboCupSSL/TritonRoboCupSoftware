package com.triton.routine.routines.decorator;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;

public class Repeater extends Decorator {
    private int times;
    private int originalTimes;

    public Repeater(Routine routine) {
        super(routine);
        this.times = -1;
        this.originalTimes = times;
    }

    public Repeater(Routine routine, int times) {
        super(routine);
        if (times < 1)
            throw new RuntimeException("Can't repeat negative times.");
        this.times = times;
        this.originalTimes = times;
    }

    @Override
    public void reset() {
        this.times = originalTimes;
    }

    @Override
    public void act(Runner runner, Context context) {
        if (routine.isFailure()) {
            fail();
        } else if (routine.isSuccess()) {
            if (times == 0) {
                succeed();
                return;
            }
            if (times > 1 || times <= -1) {
                times--;
                routine.reset();
                routine.start();
            }
        }
        if (routine.isRunning())
            routine.act(runner, context);
    }
}
