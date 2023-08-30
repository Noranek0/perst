package org.garret.perst;

import java.util.*;

public interface BitIndex<T> extends IPersistent, IResource, ITable<T> { 
    public int getMask(T obj);
    public void put(T obj, int mask);
    public IterableIterator<T> iterator(int set, int clear);
}


