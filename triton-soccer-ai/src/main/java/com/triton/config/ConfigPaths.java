package com.triton.config;

public enum ConfigPaths {
    NETWORK_CONFIG("config/network_config.yaml");

    private final String configPath;

    ConfigPaths(String configPath) {
        this.configPath = configPath;
    }

    public String getConfigPath() {
        return configPath;
    }
}