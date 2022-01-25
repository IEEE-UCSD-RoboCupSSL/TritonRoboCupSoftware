package com.triton.module;

import com.triton.skill.Skill;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class SkillRunner extends Module implements Runnable {

    public SkillRunner() {
        super();
    }
}
