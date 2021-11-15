package com.triton.module;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class A {
    private static final String EXCHANGE_A = "a";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_A, "fanout");

        while (true) {
            String msg = "A";
            channel.basicPublish(EXCHANGE_A, "", null, msg.getBytes(StandardCharsets.UTF_8));
            System.out.println("[A] Sent with RabbitMQ:\n" + msg);
            Thread.sleep(1000);
        }

//        connection.close();
//        channel.close();
    }
}
