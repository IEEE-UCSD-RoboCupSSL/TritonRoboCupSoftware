package com.triton.routine.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Context {
    private static final int SHARED = -1;

    private final Map<Integer, Map<StackId, Stack<Object>>> stacksMap;

    public Context() {
        stacksMap = new HashMap<>();
    }

    public void setSharedVariable(StackId stackID, Object object) {
        setVariable(SHARED, stackID, object);
    }

    public void setVariable(Integer id, StackId stackID, Object object) {
        if (!stackID.getStackClass().isInstance(object)) return;

        if (!stacksMap.containsKey(id)) {
            Map<StackId, Stack<Object>> stackMap = new HashMap<>();
            stacksMap.put(id, stackMap);
        }

        Map<StackId, Stack<Object>> stackMap = stacksMap.get(id);
        if (!stackMap.containsKey(stackID)) {
            Stack<Object> stack = new Stack<>();
            stackMap.put(stackID, stack);
        }

        Stack<Object> stack = stackMap.get(stackID);
        while (!stack.isEmpty())
            stack.pop();

        stack.push(object);
    }

    public Object getSharedVariable(StackId stackID) {
        return getVariable(SHARED, stackID);
    }

    public Object getVariable(Integer id, StackId stackID) {
        if (!stacksMap.containsKey(id))
            return null;

        Map<StackId, Stack<Object>> stackMap = stacksMap.get(id);
        if (!stackMap.containsKey(stackID))
            return null;

        Stack<Object> stack = stackMap.get(stackID);
        if (stack.isEmpty())
            return null;

        return stack.peek();
    }

    public void pushToSharedStack(StackId stackID, Object object) {
        pushToStack(SHARED, stackID, object);
    }

    public void pushToStack(Integer id, StackId stackID, Object object) {
        if (!stackID.getStackClass().isInstance(object)) return;

        if (!stacksMap.containsKey(id)) {
            Map<StackId, Stack<Object>> stackMap = new HashMap<>();
            stacksMap.put(id, stackMap);
        }

        Map<StackId, Stack<Object>> stackMap = stacksMap.get(id);
        if (!stackMap.containsKey(stackID)) {
            Stack<Object> stack = new Stack<>();
            stackMap.put(stackID, stack);
        }

        Stack<Object> stack = stackMap.get(stackID);
        stack.push(object);
    }

    public Object popFromSharedStack(StackId stackID) {
        return popFromStack(SHARED, stackID);
    }

    public Object popFromStack(Integer id, StackId stackID) {
        if (!stacksMap.containsKey(id))
            return null;

        Map<StackId, Stack<Object>> stackMap = stacksMap.get(id);
        if (!stackMap.containsKey(stackID))
            return null;

        Stack<Object> stack = stackMap.get(stackID);
        if (stack.isEmpty())
            return null;

        return stack.pop();
    }

    public boolean isSharedEmpty(StackId stackID) {
        return isEmpty(SHARED, stackID);
    }

    public boolean isEmpty(Integer id, StackId stackID) {
        if (!stacksMap.containsKey(id))
            return true;

        Map<StackId, Stack<Object>> stackMap = stacksMap.get(id);
        if (!stackMap.containsKey(stackID))
            return true;

        Stack<Object> stack = stackMap.get(stackID);
        return stack.isEmpty();
    }
}
