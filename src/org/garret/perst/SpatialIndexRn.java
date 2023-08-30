package org.garret.perst;

import java.util.*;

public interface SpatialIndexRn<T> extends IPersistent, IResource, ITable<T> {
    public Object[] get(RectangleRn r);
    public ArrayList<T> getList(RectangleRn r);
    public void put(RectangleRn r, T obj);
    public void remove(RectangleRn r, T obj);
    public RectangleRn getWrappingRectangle();
    public Iterator<T> iterator();
    public IterableIterator<Map.Entry<RectangleRn,T>> entryIterator();
    public IterableIterator<T> iterator(RectangleRn r); 
    public IterableIterator<Map.Entry<RectangleRn,T>> entryIterator(RectangleRn r);
    public IterableIterator<T> neighborIterator(PointRn center);
}
