package com.triton.routine.routines.leaf.context_manipulation;

import com.triton.routine.base.Context;
import com.triton.routine.base.Routine;
import com.triton.routine.base.Runner;
import com.triton.routine.base.StackId;

public class PopFromSharedStack extends Routine {
    private StackId fromStack;
    private StackId toVariable;

    public PopFromSharedStack(StackId fromStack, StackId toVariable) {
        super();
        this.fromStack = fromStack;
        this.toVariable = toVariable;
    }

    @Override
    public void reset() {
    }

    @Override
    public void act(Runner runner, Context context) {
        Object object = context.popFromSharedStack(fromStack);
        context.setSharedVariable(toVariable, object);
        succeed();
    }
}
