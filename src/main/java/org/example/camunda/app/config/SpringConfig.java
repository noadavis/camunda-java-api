package org.example.camunda.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.example.camunda.app.appdata.AppData;
import org.example.camunda.helpers.JsonAnswer;

@Configuration
public class SpringConfig {

    @Bean
    @Scope("prototype")
    public AppData appData(Authentication authentication, String title, String icon, String backPage) {
        return new AppData(authentication, title, icon, backPage);
    }

    @Bean
    @Scope("prototype")
    public JsonAnswer jsonAnswer(String value, boolean error, String description) {
        return new JsonAnswer(value, error, description);
    }

}
