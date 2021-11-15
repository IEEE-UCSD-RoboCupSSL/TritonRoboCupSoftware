import pika

count = 0
EXCHANGE_B = 'b'
EXCHANGE_C = 'c'

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()

channel.exchange_declare(exchange=EXCHANGE_B, exchange_type='fanout')
result = channel.queue_declare(queue='', exclusive=True)
queue_name = result.method.queue
channel.queue_bind(exchange=EXCHANGE_B, queue=queue_name)

channel.exchange_declare(exchange=EXCHANGE_C, exchange_type='fanout')

print("[C] Waiting for messages. To exit press CTRL+C")

def callback(ch, method, properties, body):
    global count
    input_message = body.decode('utf-8')
    print("[C] Received with RabbitMQ:\n" + input_message)
    output_message = input_message + 'C'

    channel.basic_publish(exchange=EXCHANGE_C, routing_key='', body=output_message)
    print("[C] Sent with RabbitMQ:\n" + output_message)
    count += 1

channel.basic_consume(queue=queue_name, on_message_callback=callback, auto_ack=True)
channel.start_consuming()