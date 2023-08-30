package org.garret.perst;

import java.util.*;

public interface GenericIndex<T> extends IPersistent, IResource, ITable<T> {
    public T get(Key key);
    public T get(Object key);
    public Object[] get(Key from, Key till);
    public Object[] get(Object from, Object till);
    public ArrayList<T> getList(Key from, Key till);
    public ArrayList<T> getList(Object from, Object till);
    public Object[] getPrefix(String prefix);
    public ArrayList<T> getPrefixList(String prefix);
    public ArrayList<T> prefixSearchList(String word);
    public Object[] prefixSearch(String word);

    public Iterator<T> iterator();
    public IterableIterator<T> iterator(Key from, Key till, int order);
    public IterableIterator<T> iterator(Object from, Object till, int order);
    public IterableIterator<Map.Entry<Object, T>> entryIterator();
    public IterableIterator<Map.Entry<Object, T>> entryIterator(Key from, Key till, int order);
    public IterableIterator<Map.Entry<Object, T>> entryIterator(Object from, Object till, int order);
    public IterableIterator<Map.Entry<Object, T>> entryIterator(int start, int order);
    public IterableIterator<T> prefixIterator(String prefix);
    public IterableIterator<T> prefixIterator(String prefix, int order);

    static final int ASCENT_ORDER = 0;
    static final int DESCENT_ORDER = 1;

    public Class getKeyType();
    public Class[] getKeyTypes();

    T getAt(int i);
    int indexOf(Key key);
    boolean isUnique();
}
