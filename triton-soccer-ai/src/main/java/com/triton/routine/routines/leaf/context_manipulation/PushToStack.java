package com.triton.routine.routines.leaf.context_manipulation;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import com.triton.routine.base.StackId;

public class PushToStack extends Routine {
    private final StackId fromVariable;
    private final StackId toStack;

    public PushToStack(StackId fromVariable, StackId toStack) {
        super();
        this.fromVariable = fromVariable;
        this.toStack = toStack;
    }

    @Override
    public void reset() {
    }

    @Override
    public void act(Runner runner, Context context) {
        Object object = context.getVariable(runner.getAllyId(), fromVariable);
        context.pushToStack(runner.getAllyId(), toStack, object);
        succeed();
    }
}
