package org.garret.perst;

import java.util.*;

public interface SortedCollection<T> extends IPersistent, IResource, ITable<T> {
    public T get(Object key);
    public Object[] get(Object from, Object till);
    public Object[] get(Object from, boolean fromInclusive, Object till, boolean tillInclusive);
    public ArrayList<T> getList(Object from, Object till);
    public ArrayList<T> getList(Object from, boolean fromInclusive, Object till, boolean tillInclusive);
    public boolean add(T obj);
    public boolean containsObject(T obj);
    public boolean containsKey(Object key);
    public Iterator<T> iterator();
    public IterableIterator<T> iterator(Object from, Object till);
    public IterableIterator<T> iterator(Object from, boolean fromInclusive, Object till, boolean tillInclusive);
    public PersistentComparator<T> getComparator();
}
