import yaml
import os

script_dir = os.path.dirname(__file__)
rel_path = "../config_yaml/categories.yaml"
abs_file_path = os.path.join(script_dir, rel_path)

with open(abs_file_path, 'r') as file:
    documents = yaml.full_load(file)

    for item, doc in documents.items():
        print(item, ":", doc)