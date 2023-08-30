package org.garret.perst;

import java.util.Iterator;
import java.util.Map;
import java.lang.reflect.Field;

public interface FieldIndex<T> extends GenericIndex<T> {
    public boolean put(T obj);
    public T set(T obj);
    public void append(T obj);
    public T remove(Key key);
    public T removeKey(Object key);
    public boolean containsObject(T obj);
    public IterableIterator<T> queryByExample(T obj);
    public Class getIndexedClass();
    public Field[] getKeyFields();
    public IterableIterator<T> select(String predicate);
    boolean isCaseInsensitive();
}
