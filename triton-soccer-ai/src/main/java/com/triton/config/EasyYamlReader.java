package com.triton.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

public class EasyYamlReader {
    public static Object readYaml(Config config) throws IOException {
        InputStream inputStream = EasyYamlReader.class.getClassLoader().getResourceAsStream(config.getConfigPath());
        Yaml yaml = new Yaml(new Constructor(config.getConfigClass()));
        Object returnObject = yaml.load(inputStream);
        inputStream.close();
        return returnObject;
    }
}
