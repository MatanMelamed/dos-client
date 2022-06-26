package com.ermetic.dosclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {


    @Bean
    @ConfigurationProperties(prefix = "dos-service")
    public DosServiceConfig dosServiceConfig(){
        return new DosServiceConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "dos-client")
    public DosClientConfig dosClientConfig() {
        return new DosClientConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "dos-task")
    public DosTaskConfig dosTaskConfig() {
        return new DosTaskConfig();
    }
}
