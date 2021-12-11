package com.triton;

import com.rabbitmq.client.*;
import com.triton.publisher_consumer.Exchange;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static com.triton.publisher_consumer.EasySerialize.standardDeserialize;
import static com.triton.publisher_consumer.EasySerialize.standardSerialize;

public abstract class Module {
    private Channel channel;

    public Module() throws IOException, TimeoutException {
        setupRabbitMQ();
    }

    protected void setupRabbitMQ() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        setChannel(connection.createChannel());
    }

    protected void declarePublish(Exchange exchange) throws IOException {
        getChannel().exchangeDeclare(exchange.getExchangeName(), "fanout");
    }

    protected void declareConsume(Exchange exchange, Consumer<Object> messageConsumer) throws IOException {
        String exchangeName = exchange.getExchangeName();
        getChannel().exchangeDeclare(exchangeName, "fanout");
        String queueName = getChannel().queueDeclare().getQueue();
        getChannel().queueBind(queueName, exchangeName, "");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            try {
                Object object = standardDeserialize(message.getBody());
                messageConsumer.accept(object);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        };

        CancelCallback cancelCallback = (consumerTag) -> {
        };

        getChannel().basicConsume(queueName, true, deliverCallback, cancelCallback);
    }

    protected void publish(Exchange exchange, Object object) throws IOException {
        getChannel().basicPublish(exchange.getExchangeName(), "", null, standardSerialize(object));
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
