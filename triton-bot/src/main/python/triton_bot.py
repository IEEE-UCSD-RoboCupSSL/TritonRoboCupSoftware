import argparse
import sys

from constant.runtime_constants import RuntimeConstants
from constant.team import Team
from module.interface_module.ai_interface import AI_Interface
from module.interface_module.simulator_robot_control_interface import \
    SimulatorRobotControlInterface
from module.processing_module.robot_control_global_processor import \
    RobotControlGlobalProcessor
from module.processing_module.robot_control_local_processor import \
    RobotControlLocalProcessor
from module.processing_module.robot_control_wheel_processor import \
    RobotControlWheelProcessor
from module.processing_module.triton_bot_message_processor import \
    TritonBotMessageProcessor


class TritonBot:
    def __init__(self):
        self.start_modules()

    def start_modules(self):
        AI_Interface().start()
        SimulatorRobotControlInterface().start()

        TritonBotMessageProcessor().start()
        RobotControlGlobalProcessor().start()
        RobotControlLocalProcessor().start()
        RobotControlWheelProcessor().start()


def parseTeam(teamStr):
    for matchTeam in Team:
        if (matchTeam.value == args.team):
            return matchTeam
    return None


parser = argparse.ArgumentParser()
parser.add_argument('--team', type=str, required=True)
parser.add_argument('--id', type=int, required=True)
args = parser.parse_args()


team = parseTeam(args.team)
if (team == None):
    print("Unable to parse team.")
    sys.exit(1)

RuntimeConstants.team = team
RuntimeConstants.id = args.id


TritonBot()
