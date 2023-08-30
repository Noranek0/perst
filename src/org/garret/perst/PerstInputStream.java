package org.garret.perst;

import java.io.*;

public abstract class PerstInputStream extends DataInputStream {
    public abstract Object readObject() throws IOException;

    public abstract String readString() throws IOException;

    public PerstInputStream(InputStream stream) {
        super(stream);
    }
}
