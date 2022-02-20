package com.triton.routine.routines.leaf.context_manipulation;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import com.triton.routine.base.StackId;

public class SetSharedVariable extends Routine {
    private StackId stackId;
    private Object object;

    public SetSharedVariable(StackId stackId, Object object) {
        super();
        this.stackId = stackId;
        this.object = object;
    }

    @Override
    public void reset() {
    }

    @Override
    public void act(Runner runner, Context context) {
        context.setSharedVariable(stackId, object);
        succeed();
    }
}
