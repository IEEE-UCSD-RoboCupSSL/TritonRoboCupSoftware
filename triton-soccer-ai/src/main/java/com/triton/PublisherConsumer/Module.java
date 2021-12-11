package com.triton.PublisherConsumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static com.triton.PublisherConsumer.EasySerialize.standardDeserialize;
import static com.triton.PublisherConsumer.EasySerialize.standardSerialize;

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

    protected void declareConsume(Exchange exchange, Consumer<Object> deliveryConsumer, Consumer<String> cancelConsumer) throws IOException {
        String exchangeName = exchange.getExchangeName();
        getChannel().exchangeDeclare(exchangeName, "fanout");
        String queueName = getChannel().queueDeclare().getQueue();
        getChannel().queueBind(queueName, exchangeName, "");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            try {
                Object object = standardDeserialize(message.getBody());
                deliveryConsumer.accept(object);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        };

        getChannel().basicConsume(queueName, true, deliverCallback, (CancelCallback) cancelConsumer);
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
