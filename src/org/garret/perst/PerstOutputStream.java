package org.garret.perst;

import java.io.*;

public abstract class PerstOutputStream extends DataOutputStream {
    public abstract void writeObject(Object obj) throws IOException;

    public abstract void writeString(String str) throws IOException;

    public PerstOutputStream(OutputStream stream) {
        super(stream);
    }
}
