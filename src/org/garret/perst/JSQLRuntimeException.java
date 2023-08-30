package org.garret.perst;

public class JSQLRuntimeException extends RuntimeException {
    public JSQLRuntimeException(String message, Class target, String fieldName) {
        super(message);
        this.target = target;
        this.fieldName = fieldName;
    }

    public Class getTarget() {
        return target;
    }

    public String getFieldName() {
        return fieldName;
    }

    String fieldName;
    Class target;
}
