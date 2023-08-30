package org.garret.perst;

public class JSQLNoSuchFieldException extends JSQLRuntimeException { 
    public JSQLNoSuchFieldException(Class target, String fieldName) { 
        super("Dynamic lookup failed for field ", target, fieldName);
    }
}



