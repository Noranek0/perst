package org.garret.perst;

public abstract class PersistentComparator<T> extends Persistent {
    public abstract int compareMembers(T m1, T m2);
    public abstract int compareMemberWithKey(T mbr, Object key);
}
