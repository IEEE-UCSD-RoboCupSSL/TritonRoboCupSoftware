import os
import yaml


def read_config(config):
    script_dir = os.path.dirname(__file__)
    rel_path = config.value
    abs_file_path = os.path.join(script_dir, rel_path)

    with open(abs_file_path, 'r') as file:
        data = yaml.full_load(file)
        return data