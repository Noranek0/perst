package org.garret.perst;

import java.util.Iterator;

public class ThreadSafeIterator<T> extends IterableIterator<T> {
    private IResource collection;
    private Iterator<T> iterator;
    private T next;

    public boolean hasNext() {
        boolean result;
        if (next == null) {
            collection.sharedLock();
            if (iterator.hasNext()) {
                next = iterator.next();
                result = true;
            } else {
                result = false;
            }
            collection.unlock();
        } else {
            result = true;
        }
        return result;
    }

    public T next() {
        T obj = next;
        if (obj == null) {
            collection.sharedLock();
            obj = iterator.next();
            collection.unlock();
        } else {
            next = null;
        }
        return obj;
    }

    public ThreadSafeIterator(IResource collection, Iterator<T> iterator) {
        this.collection = collection;
        this.iterator = iterator;
    }

    public void remove() {
        collection.exclusiveLock();
        iterator.remove();
        collection.unlock();
        next = null;
    }
}
