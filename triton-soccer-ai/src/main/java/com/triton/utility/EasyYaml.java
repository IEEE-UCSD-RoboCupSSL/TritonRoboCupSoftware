package com.triton.utility;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class EasyYaml {
    public static Object readYaml(String path, Class yamlClass) {
        InputStream inputStream = EasyYaml.class.getClassLoader().getResourceAsStream(path);
        Yaml yaml = new Yaml(new Constructor(yamlClass));
        return yaml.load(inputStream);

    }
}