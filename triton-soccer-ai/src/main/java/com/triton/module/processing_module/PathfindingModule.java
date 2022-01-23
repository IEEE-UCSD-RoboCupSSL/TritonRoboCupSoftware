package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.module.Module;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.triton.ObjectWithMetadata.Ball;
import static proto.triton.ObjectWithMetadata.Robot;

public class PathfindingModule extends Module {
    private Ball ball;
    private HashMap<Integer, Robot> allies;
    private HashMap<Integer, Robot> foes;

    public PathfindingModule() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void prepare() {
        super.prepare();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_FILTERED_BIASED_BALLS, this::callbackBalls);
        declareConsume(AI_FILTERED_BIASED_ALLIES, this::callbackAllies);
        declareConsume(AI_FILTERED_BIASED_FOES, this::callbackFoes);
    }

    private void callbackBalls(String s, Delivery delivery) {
        this.ball = (Ball) simpleDeserialize(delivery.getBody());
    }

    private void callbackAllies(String s, Delivery delivery) {
        this.allies = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }

    private void callbackFoes(String s, Delivery delivery) {
        this.foes = (HashMap<Integer, Robot>) simpleDeserialize(delivery.getBody());
    }
}
