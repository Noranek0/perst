package org.garret.perst;

import java.util.*;

public interface SpatialIndex<T> extends IPersistent, IResource, ITable<T> {
    public Object[] get(Rectangle r);
    public ArrayList<T> getList(Rectangle r);
    public void put(Rectangle r, T obj);
    public void remove(Rectangle r, T obj);
    public Rectangle getWrappingRectangle();
    public Iterator<T> iterator();
    public IterableIterator<Map.Entry<Rectangle,T>> entryIterator();
    public IterableIterator<T> iterator(Rectangle r);
    public IterableIterator<Map.Entry<Rectangle,T>> entryIterator(Rectangle r);
    public IterableIterator<T> neighborIterator(int x, int y);
}

