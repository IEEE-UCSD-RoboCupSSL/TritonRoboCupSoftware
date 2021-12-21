package com.triton.config;

public enum Config {
    NETWORK_CONFIG("../config_yaml/network_config.yaml", NetworkConfig.class),
    OBJECT_CONFIG("../config_yaml/object_config.yaml", ObjectConfig.class),
    DISPLAY_CONFIG("../config_yaml/display_config.yaml", DisplayConfig.class),
    ;

    private final String configPath;
    private final Class configClass;

    Config(String configPath, Class configClass) {
        this.configPath = configPath;
        this.configClass = configClass;
    }

    public String getConfigPath() {
        return configPath;
    }

    public Class getConfigClass() {
        return configClass;
    }
}