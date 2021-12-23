from enum import Enum
import enum


class Exchange(Enum):
    TB_GLOBAL_COMMAND = enum.auto()
    TB_LOCAL_COMMAND = enum.auto()
    TB_WHEEL_COMMAND = enum.auto()
    TB_ROBOT_CONTROL = enum.auto()