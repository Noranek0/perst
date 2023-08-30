package org.garret.perst;

import java.util.*;

public interface MultidimensionalIndex<T> extends IPersistent, IResource, ITable<T> {
    public MultidimensionalComparator<T> getComparator();

    public Iterator<T> iterator();
    public IterableIterator<T> iterator(T pattern);
    public IterableIterator<T> iterator(T low, T high);
    public ArrayList<T> queryByExample(T pattern);
    public ArrayList<T> queryByExample(T low, T high);

    public void optimize();
    public int getHeight();
}
