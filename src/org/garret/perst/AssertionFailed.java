package org.garret.perst;

public class AssertionFailed extends Error {
    public AssertionFailed() { 
        super("Assertion failed");
    }

    public AssertionFailed(String description) { 
        super("Assertion '" + description + "' failed");
    }
}
