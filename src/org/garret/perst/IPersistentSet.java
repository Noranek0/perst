package org.garret.perst;

import java.util.Set;
import java.util.Iterator;

public interface IPersistentSet<T> extends IPersistent, IResource, Set<T>, ITable<T> {
    public IterableIterator<T> join(Iterator<T> iterator);
}
