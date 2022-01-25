from math import degrees
from re import L
from config.config_path import ConfigPath
from config.config_reader import read_config
from constant.runtime_constants import RuntimeConstants
from constant.team import Team
from generated_sources.proto.triton_bot_communication_pb2 import \
    TritonBotMessage
from generated_sources.proto.ssl_simulation_robot_feedback_pb2 import RobotFeedback
from generated_sources.proto.messages_robocup_ssl_detection_pb2 import SSL_DetectionRobot
from messaging.exchange import Exchange
from module.module import Module
from networking.udp_server import UDP_Server


class AI_Interface(Module):
    def __init__(self):
        super().__init__()

    def load_config(self):
        super().load_config()
        self.network_config = read_config(
            ConfigPath.NETWORK_CONFIG)

    def prepare(self):
        super().prepare()

        self.feedback = RobotFeedback()
        self.feedback.id = RuntimeConstants.id
        self.feedback.dribbler_ball_contact = False
        
        self.setup_client()

    def declare_exchanges(self):
        super().declare_exchanges()
        self.declare_consume(exchange=Exchange.TB_FEEDBACK, callback=self.callback_feedback)
        self.declare_publish(exchange=Exchange.TB_MESSAGE)

    def run(self):
        super().run()
        self.consume()

    def setup_client(self):
        if (RuntimeConstants.team == Team.YELLOW):
            server_port = self.network_config['tritonBotPortBaseYellow'] + \
                RuntimeConstants.id * self.network_config['tritonBotPortIncr']
        else:
            server_port = self.network_config['tritonBotPortBaseBlue'] + \
                RuntimeConstants.id * self.network_config['tritonBotPortIncr']

        self.server = UDP_Server(
            server_port=server_port, callback=self.callback_message)
        self.server.start()

    def callback_feedback(self, ch, method, properties, body):
        feedback = RobotFeedback()
        feedback.ParseFromString(body)
        self.feedback = feedback

    def callback_message(self, bytes):
        message = TritonBotMessage()
        message.ParseFromString(bytes)

        self.publish(exchange=Exchange.TB_MESSAGE, object=message)
        return self.feedback.SerializeToString()