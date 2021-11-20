package com.triton.module;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class AI_A {
    private static final String EXCHANGE_A = "a";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_A, "fanout");

        while (true) {
            String outMsg = "A";
            channel.basicPublish(EXCHANGE_A, "", null, outMsg.getBytes(StandardCharsets.UTF_8));
            System.out.println("[AI_A] Sent with RabbitMQ:\n" + outMsg);
            Thread.sleep(1000);
        }

//        connection.close();
//        channel.close();
    }
}
