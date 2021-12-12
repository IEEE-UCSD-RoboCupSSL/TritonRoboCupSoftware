package com.triton.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class EasyYamlReader {
    public static Object readYaml(String path, Class yamlClass) {
        InputStream inputStream = EasyYamlReader.class.getClassLoader().getResourceAsStream(path);
        Yaml yaml = new Yaml(new Constructor(yamlClass));
        return yaml.load(inputStream);
    }
}
