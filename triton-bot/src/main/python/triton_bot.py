import argparse
import sys

from constant.team import Team
from constant.runtime_constants import RuntimeConstants
from module.interface_module.ai_interface import AI_Interface


class TritonBot:
    def __init__(self):
        self.start_modules()

    def start_modules(self):
        AI_Interface().start()


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
