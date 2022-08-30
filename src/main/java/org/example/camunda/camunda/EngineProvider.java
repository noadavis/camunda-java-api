package org.example.camunda.camunda;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EngineProvider {

    ProcessEngine defaultProcessEngine;

    private void getEngine() {
        /*
        ProcessEngine defaultProcessEngine = BpmPlatform.getDefaultProcessEngine();
        if (defaultProcessEngine != null) {
            this.defaultProcessEngine = defaultProcessEngine;
        } else {
            this.defaultProcessEngine = ProcessEngines.getDefaultProcessEngine(false);
        }
        */
        ProcessEngine platformEngine = BpmPlatform.getDefaultProcessEngine();
        if (platformEngine != null) {
            this.defaultProcessEngine = platformEngine;
        } else {
            this.defaultProcessEngine = ProcessEngines.getDefaultProcessEngine(false);
        }
    }

    public EngineProvider() {
        getEngine();
    }

    public ProcessEngine getDefaultProcessEngine() {
        if (defaultProcessEngine == null) getEngine();
        return defaultProcessEngine;
    }

    public ProcessEngine getProcessEngine(String name) {
        return ProcessEngines.getProcessEngine(name);
    }

    public Set<String> getProcessEngineNames() {
        return ProcessEngines.getProcessEngines().keySet();
    }
}
