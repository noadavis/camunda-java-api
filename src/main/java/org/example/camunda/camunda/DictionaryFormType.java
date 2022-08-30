package org.example.camunda.camunda;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.form.type.SimpleFormFieldType;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.LinkedHashMap;
import java.util.Map;

public class DictionaryFormType extends SimpleFormFieldType {
    public static final String TYPE_NAME = "dictionary";
    protected Map<String, String> values;

    public DictionaryFormType(Map<String, String> values) {
        if (values == null) {
            this.values = new LinkedHashMap<>();
        } else this.values = values;
    }

    public String getName() {
        return TYPE_NAME;
    }

    public Object getInformation(String key) {
        return key.equals("values") ? this.values : null;
    }

    public TypedValue convertValue(TypedValue propertyValue) {
        Object value = propertyValue.getValue();
        if (value != null && !String.class.isInstance(value)) {
            throw new ProcessEngineException("Value '" + value + "' is not of type String.");
        } else {
            this.validateValue(value);
            return Variables.stringValue((String)value, propertyValue.isTransient());
        }
    }

    protected void validateValue(Object value) {
        //if (value != null && this.values != null && !this.values.containsKey(value)) {
        //    throw new ProcessEngineException("Invalid value for enum form property: " + value);
        //}
    }

    public Map<String, String> getValues() {
        return this.values;
    }

    public Object convertFormValueToModelValue(Object propertyValue) {
        this.validateValue(propertyValue);
        return propertyValue;
    }

    public String convertModelValueToFormValue(Object modelValue) {
        if (modelValue != null) {
            if (!(modelValue instanceof String)) {
                throw new ProcessEngineException("Model value should be a String");
            }
            this.validateValue(modelValue);
        }
        return (String)modelValue;
    }

}
