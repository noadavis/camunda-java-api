package org.example.camunda.camunda;

import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration;
import org.springframework.stereotype.Component;

@Component
public class CamundaConfig extends AbstractCamundaConfiguration {
    @Override
    public void preInit(SpringProcessEngineConfiguration config) {
        config.getCustomFormTypes().add(new DictionaryFormType(null));
    }
}
