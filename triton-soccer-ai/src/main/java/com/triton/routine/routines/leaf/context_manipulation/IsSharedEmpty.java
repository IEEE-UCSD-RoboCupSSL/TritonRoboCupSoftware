package com.triton.routine.routines.leaf.context_manipulation;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import com.triton.routine.base.StackId;

public class IsSharedEmpty extends Routine {
    private final StackId stackId;

    public IsSharedEmpty(StackId stackId) {
        super();
        this.stackId = stackId;
    }

    @Override
    public void reset() {
    }

    @Override
    public void act(Runner runner, Context context) {
        if (context.isSharedEmpty(stackId))
            succeed();
        else
            fail();
    }
}
