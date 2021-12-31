from generated_sources.proto.messages_robocup_ssl_detection_pb2 import SSL_DetectionRobot
from messaging.exchange import Exchange
from module.module import Module


class VisionFilter(Module):
    def __init__(self):
        super().__init__()

    def load_config(self):
        super().load_config()

    def prepare(self):
        super().prepare()

    def declare_exchanges(self):
        super().declare_exchanges()
        self.declare_consume(Exchange.TB_RAW_VISION, self.callback_vision)
        self.declare_publish(Exchange.TB_VSION)

    def run(self):
        super().run()
        self.consume()
    
    def callback_vision(self, ch, method, properties, body):
        vision = SSL_DetectionRobot()
        vision.ParseFromString(body)
        self.publish(exchange=Exchange.TB_VSION, object=vision)