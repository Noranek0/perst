package org.garret.perst;

import java.util.*;

public abstract class IterableIterator<T> implements Iterable<T>, Iterator<T> {
    public Iterator<T> iterator() {
        return this;
    }
    public T first() {
        return hasNext() ? next() : null;
    }
    public int size() {
        int count = 0;
        for (T obj : this) {
            count += 1;
        }
        return count;
    }
    public void remove() {
        throw new UnsupportedOperationException();
    }
    public ArrayList<T> toList() {
        ArrayList<T> list = new ArrayList<T>();
        for (T obj : this) {
            list.add(obj);
        }
        return list;
    }
}
