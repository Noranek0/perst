package org.garret.perst;

import java.util.*;

public interface IPersistentMap<K extends Comparable, V> extends SortedMap<K, V>, IPersistent, IResource {
    Entry<K, V> getEntry(Object key);
    public Iterator<V> select(Class cls, String predicate);
}