package org.garret.perst;

import java.util.*;

public interface TimeSeries<T extends TimeSeries.Tick> extends IPersistent, IResource, ITable<T> {
    public interface Tick extends IValue {
        long getTime();
    }

    public static abstract class Block extends Persistent {
        public long timestamp;
        public int used;
        public abstract Tick[] getTicks();
    }

    boolean add(T tick);
    boolean add(T tick, boolean reverse);

    ArrayList<T> elements();
    Iterator<T> iterator();
    IterableIterator<T> iterator(Date from, Date till);
    IterableIterator<T> iterator(boolean ascent);
    IterableIterator<T> iterator(Date from, Date till, boolean ascent);

    Date getFirstTime();
    Date getLastTime();

    long countTicks();

    T getTick(Date timestamp);

    boolean has(Date timestamp);
    int remove(Date from, Date till);
}
