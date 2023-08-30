package org.garret.perst;

import java.util.*;

public interface SpatialIndexR2<T> extends IPersistent, IResource, ITable<T> {
    public Object[] get(RectangleR2 r);
    public ArrayList<T> getList(RectangleR2 r);
    public void put(RectangleR2 r, T obj);
    public void remove(RectangleR2 r, T obj);
    public RectangleR2 getWrappingRectangle();
    public Iterator<T> iterator();
    public IterableIterator<Map.Entry<RectangleR2,T>> entryIterator();
    public IterableIterator<T> iterator(RectangleR2 r); 
    public IterableIterator<Map.Entry<RectangleR2,T>> entryIterator(RectangleR2 r);
    public IterableIterator<T> neighborIterator(double x, double y);

    public void print();
}
