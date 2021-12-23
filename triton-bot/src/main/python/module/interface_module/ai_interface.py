from config.config_path import ConfigPath
from config.config_reader import read_config
from generated_sources.proto.triton_bot_init_pb2 import Init
from constant.runtime_constants import RuntimeConstants
from generated_sources.proto.ssl_simulation_robot_control_pb2 import RobotCommand
from constant.team import Team
from networking.udp_server import UDP_Server
from messaging.exchange import Exchange
from module.module import Module


class AI_Interface(Module):
    def __init__(self):
        super().__init__()

    def load_config(self):
        super().load_config()
        self.network_config = read_config(
            ConfigPath.NETWORK_CONFIG)

    def prepare(self):
        super().prepare()
        self.init_received = False
        self.setup_client()

    def declare_exchanges(self):
        super().declare_exchanges()
        self.declare_publish(Exchange.TB_GLOBAL_COMMAND)
        self.declare_publish(Exchange.TB_LOCAL_COMMAND)
        self.declare_publish(Exchange.TB_WHEEL_COMMAND)

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
            server_port=server_port, callback=self.callback_raw_command)
        self.server.start()

    def callback_raw_command(self, bytes):
        command = RobotCommand()
        command.ParseFromString(bytes)

        exchange = Exchange.TB_WHEEL_COMMAND
        if (command.HasField('move_command')):
            if (command.move_command.HasField('global_velocity')):
                exchange = Exchange.TB_GLOBAL_COMMAND
            elif (command.move_command.HasField('local_velocity')):
                exchange = Exchange.TB_LOCAL_COMMAND

        self.publish(exchange=exchange, object=command)