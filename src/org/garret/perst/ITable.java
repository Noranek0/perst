package org.garret.perst;

import java.util.*;

public interface ITable<T> extends Collection<T> {
    public IterableIterator<T> select(Class cls, String predicate);
    public void deallocateMembers();
}