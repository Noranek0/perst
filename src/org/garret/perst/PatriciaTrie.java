package org.garret.perst;

import java.util.*;

public interface PatriciaTrie<T> extends IPersistent, IResource, ITable<T> { 
    T add(PatriciaTrieKey key, T obj);
    T findBestMatch(PatriciaTrieKey key);
    T findExactMatch(PatriciaTrieKey key);
    T remove(PatriciaTrieKey key);

    ArrayList<T> elements();
}