package com.triton.routine.routines.leaf.context_manipulation;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import com.triton.routine.base.StackId;

public class PushToSharedStack extends Routine {
    private StackId fromVariable;
    private StackId toStack;

    public PushToSharedStack(StackId fromVariable, StackId toStack) {
        super();
        this.fromVariable = fromVariable;
        this.toStack = toStack;
    }

    @Override
    public void reset() {
    }

    @Override
    public void act(Runner runner, Context context) {
        Object object = context.getSharedVariable(fromVariable);
        context.pushToSharedStack(toStack, object);
        succeed();
    }
}
