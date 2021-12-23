package com.triton.config;

public enum ConfigPath {
    NETWORK_CONFIG("../config/network_config.yaml", NetworkConfig.class),
    OBJECT_CONFIG("../config/object_config.yaml", ObjectConfig.class),
    DISPLAY_CONFIG("../config/display_config.yaml", DisplayConfig.class),
    ;

    private final String configPath;
    private final Class configClass;

    ConfigPath(String configPath, Class configClass) {
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