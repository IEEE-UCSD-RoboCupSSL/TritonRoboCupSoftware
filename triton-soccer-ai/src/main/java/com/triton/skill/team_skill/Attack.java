package com.triton.skill.team_skill;

import com.triton.module.Module;
import com.triton.skill.Skill;
import proto.triton.FilteredObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static proto.triton.FilteredObject.*;

public class Attack extends Skill {

    public Attack(Module module) {
        super(module);
    }

    @Override
    protected void execute() {

    }

    @Override
    protected void declarePublishes() throws IOException, TimeoutException {

    }
}
