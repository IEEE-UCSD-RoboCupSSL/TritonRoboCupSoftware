package com.triton.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

public class EasyYamlReader {
    public static Object readYaml(String path, Class yamlClass) throws IOException {
        InputStream inputStream = EasyYamlReader.class.getClassLoader().getResourceAsStream(path);
        Yaml yaml = new Yaml(new Constructor(yamlClass));
        Object returnObject = yaml.load(inputStream);
        inputStream.close();
        return returnObject;
    }
}
