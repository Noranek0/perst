package org.garret.perst;

public interface IndexProvider {
    GenericIndex getIndex(Class cls, String key);
}