package org.garret.perst;

public class JSQLNullPointerException extends JSQLRuntimeException {
    public JSQLNullPointerException(Class target, String fieldName) {
        super("Dereferencing null reference ", target, fieldName);
    }
}
