package org.garret.perst;

public interface EmbeddedLink<T> extends Link<T> {
    void setOwner(Object obj);
    Object getOwner();
}