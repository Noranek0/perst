package org.garret.perst;

import java.util.*;

public interface IPersistentHash<K, V> extends Map<K, V>, IPersistent, IResource {
    Entry<K, V> getEntry(Object key);
    public Iterator<V> select(Class cls, String predicate);
}