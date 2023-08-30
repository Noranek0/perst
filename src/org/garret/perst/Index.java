package org.garret.perst;

import java.util.*;

public interface Index<T> extends GenericIndex<T> {
    public boolean put(Key key, T obj);
    public boolean put(Object key, T obj);
    public T set(Key key, T obj);
    public T set(Object key, T obj);
    public void remove(Key key, T obj);
    public void remove(Object key, T obj);
    public T remove(Key key);
    public T remove(String key);
    public boolean unlink(Key key, T obj);

    public T removeKey(Object key);
}
