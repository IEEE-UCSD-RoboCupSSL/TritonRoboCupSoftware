package com.triton.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfigReader {
    public static Object readConfig(ConfigPath configPath) throws IOException {
        InputStream inputStream = new FileInputStream(configPath.getConfigPath());
        Yaml yaml = new Yaml(new Constructor(configPath.getConfigClass()));
        Object returnObject = yaml.load(inputStream);
        inputStream.close();
        return returnObject;
    }
}
