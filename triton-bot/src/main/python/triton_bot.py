from modules.processors import moduleA
from modules.processors import moduleB


def main():
    moduleA.ModuleA().start()
    moduleB.ModuleB().start()

main()
