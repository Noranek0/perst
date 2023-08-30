package org.garret.perst;

import java.io.*;

public interface CustomSerializer {
    void pack(Object obj, PerstOutputStream out) throws IOException;
    Object unpack(PerstInputStream in) throws IOException;
    void unpack(Object obj, PerstInputStream in) throws IOException;
    Object create(Class cls);
    Object parse(String str) throws Exception;
    String print(Object str);

    boolean isApplicable(Class cls);
    boolean isEmbedded(Object obj);
}