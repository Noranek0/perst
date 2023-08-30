package org.garret.perst;

public class CompileError extends RuntimeException { 
    public CompileError(String msg, int  pos) { 
	super(msg + " in position " + pos);
    }
}
