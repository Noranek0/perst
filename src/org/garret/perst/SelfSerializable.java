package org.garret.perst;

public interface SelfSerializable
{
    void pack(PerstOutputStream out) throws java.io.IOException;
    void unpack(PerstInputStream in) throws java.io.IOException;
}