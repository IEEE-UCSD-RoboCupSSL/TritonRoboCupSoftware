package com.triton.routine.base;

import com.triton.search.implementation.PathfindGridGroup;
import com.triton.util.Vector2d;

public enum StackId {
    PATHFIND_GROUP(PathfindGridGroup.class),
    DRIBBLE_ON(Boolean.class),
    KICK_SPEED(Float.class),
    TARGET_VEL(Vector2d.class),
    TARGET_ANGULAR(Float.class),
    ;

    private final Class stackClass;

    StackId(Class stackClass) {
        this.stackClass = stackClass;
    }

    public Class getStackClass() {
        return stackClass;
    }
}
