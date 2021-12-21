package com.triton.publisher_consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static com.triton.publisher_consumer.EasySerialize.standardDeserialize;
import static com.triton.publisher_consumer.EasySerialize.standardSerialize;

public abstract class Module extends Thread {
    private static final String CONNECTION_FACTORY_HOST = "localhost";
    private static final String EXCHANGE_MODE = "fanout";

    private Channel channel;

    public Module() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(CONNECTION_FACTORY_HOST);
        Connection connection = factory.newConnection();
        channel = connection.createChannel();

        loadConfig();
    }

    protected void loadConfig() throws IOException {
    }

    /**
     * Override to declare exchanges.
     *
     * @throws IOException
     * @throws TimeoutException
     */
    protected void declareExchanges() throws IOException, TimeoutException {
    }

    /**
     * Declares an exchange to publish to
     *
     * @param exchange the exchange
     * @throws IOException
     */
    protected void declarePublish(Exchange exchange) throws IOException {
        channel.exchangeDeclare(exchange.name(), EXCHANGE_MODE);
    }

    /**
     * Declares an exchange to consume from. The messageConsumer function will be called when an message is received
     * from the exchange.
     *
     * @param exchange        the exchange to consume from
     * @param messageConsumer a function that accepts an object
     * @throws IOException
     */
    protected void declareConsume(Exchange exchange, Consumer<Object> messageConsumer) throws IOException {
        String exchangeName = exchange.name();
        channel.exchangeDeclare(exchangeName, EXCHANGE_MODE);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            try {
                Object object = standardDeserialize(message.getBody());
                messageConsumer.accept(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

    /**
     * Publishes to an exchange
     *
     * @param exchange the exchange to publish to
     * @param object   the object to send
     * @throws IOException
     */
    protected void publish(Exchange exchange, Object object) throws IOException {
        channel.basicPublish(exchange.name(), "", null, standardSerialize(object));
    }}
