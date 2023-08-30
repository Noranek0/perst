package org.garret.perst;

import java.util.*;

public interface Link<T> extends ITable<T>, List<T>, RandomAccess {
  public void setSize(int newSize);
  boolean isEmpty();
  public T get(int i);
  public Object getRaw(int i);
  public T set(int i, T obj);
  public void setObject(int i, T obj);
  public void removeObject(int i);
  public void insert(int i, T obj);
  public void addAll(T[] arr);
  public void addAll(T[] arr, int from, int length);
  public boolean addAll(Link<T> link);
  public Object[] toRawArray();
  public <T> T[] toArray(T[] arr);
  public boolean containsObject(T obj);
  public boolean containsElement(int i, T obj);
  public int indexOfObject(Object obj);
  public int lastIndexOfObject(Object obj);
  public void clear();
  public Iterator<T> iterator();
  public void unpin();
  public void pin();
}
