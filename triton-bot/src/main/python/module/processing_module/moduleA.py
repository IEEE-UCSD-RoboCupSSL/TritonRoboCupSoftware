from messaging.exchange import Exchange
from module.module import Module


class ModuleA(Module):
    def __init__(self):
        super().__init__()

    def declare_exchanges(self):
        super().declare_exchanges()
        self.declare_publish(exchange=Exchange.DATA_A)

    def run(self):
        super().run()

        while (True):
            self.publish(exchange=Exchange.DATA_A, object="asdf")
