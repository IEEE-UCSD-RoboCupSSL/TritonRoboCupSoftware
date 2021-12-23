import os
import yaml


def read_config(config):
    rel_path = config.value
    abs_file_path = os.path.join("../../../config", rel_path)

    with open(abs_file_path, 'r') as file:
        data = yaml.safe_load(file)
        return data