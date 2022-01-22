package com.triton.module.processing_module;

import com.rabbitmq.client.Delivery;
import com.triton.constant.RuntimeConstants;
import com.triton.constant.Team;
import com.triton.helper.ConvertCoordinate;
import com.triton.module.Module;
import proto.vision.MessagesRobocupSslDetection.SSL_DetectionFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.triton.messaging.Exchange.*;
import static com.triton.messaging.SimpleSerialize.simpleDeserialize;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionBall;
import static proto.vision.MessagesRobocupSslDetection.SSL_DetectionRobot;
import static proto.vision.MessagesRobocupSslGeometry.*;
import static proto.vision.MessagesRobocupSslWrapper.SSL_WrapperPacket;

public class FilterModule extends Module {
    private LinkedList<ArrayList<SSL_DetectionBall>> aggregatedBalls;
    private HashMap<Integer, LinkedList<SSL_DetectionRobot>> aggregatedAllies;
    private HashMap<Integer, LinkedList<SSL_DetectionRobot>> aggregatedFoes;

    public FilterModule() throws IOException, TimeoutException {
        super();
    }

    @Override
    protected void prepare() {
        super.prepare();

        aggregatedBalls = new LinkedList<>();
        aggregatedAllies = new HashMap<>();
        aggregatedFoes = new HashMap<>();
    }

    @Override
    protected void declareExchanges() throws IOException, TimeoutException {
        super.declareExchanges();
        declareConsume(AI_BIASED_BALLS, this::callbackBalls);
        declareConsume(AI_BIASED_ALLIES, this::callbackAllies);
        declareConsume(AI_BIASED_FOES, this::callbackFoes);

        declarePublish(AI_FILTERED_BIASED_BALLS);
        declarePublish(AI_FILTERED_BIASED_ALLIES);
        declarePublish(AI_FILTERED_BIASED_FOES);
    }

    private void callbackBalls(String s, Delivery delivery) {
        ArrayList<SSL_DetectionBall> balls;
        try {
            balls = (ArrayList<SSL_DetectionBall>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            publish(AI_FILTERED_BIASED_BALLS, balls);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void callbackAllies(String s, Delivery delivery) {
        HashMap<Integer, SSL_DetectionRobot> allies;
        try {
            allies = (HashMap<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            publish(AI_FILTERED_BIASED_ALLIES, allies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void callbackFoes(String s, Delivery delivery) {
        HashMap<Integer, SSL_DetectionRobot> foes;
        try {
            foes = (HashMap<Integer, SSL_DetectionRobot>) simpleDeserialize(delivery.getBody());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            publish(AI_FILTERED_BIASED_FOES, foes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
