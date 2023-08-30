package org.garret.perst;

import java.util.*;
import org.garret.perst.impl.QueryImpl;


public class L2List extends L2ListElem implements ITable {
    private int nElems;
    private int updateCounter;

    public synchronized L2ListElem head() {
        return next != this ? next : null;
    }
    public synchronized L2ListElem tail() {
        return prev != this ? prev : null;
    }
    public synchronized void clear() {
        modify();
        next = prev = this;
        nElems = 0;
        updateCounter += 1;
    }
    public void deallocateMembers() {
        Iterator i = iterator();
        while (i.hasNext()) {
            ((IPersistent) i.next()).deallocate();
        }
        clear();
    }
    public synchronized void prepend(L2ListElem elem) {
        modify();
        next.modify();
        elem.modify();
        elem.next = next;
        elem.prev = this;
        next.prev = elem;
        next = elem;
        nElems += 1;
        updateCounter += 1;
    }
    public synchronized void append(L2ListElem elem) {
        modify();
        prev.modify();
        elem.modify();
        elem.next = this;
        elem.prev = prev;
        prev.next = elem;
        prev = elem;
        nElems += 1;
        updateCounter += 1;
    }
    public synchronized void remove(L2ListElem elem) {
        modify();
        elem.prev.modify();
        elem.next.modify();
        elem.next.prev = elem.prev;
        elem.prev.next = elem.next;
        nElems -= 1;
        updateCounter += 1;
    }
    public synchronized boolean isEmpty() {
        return next == this;
    }
    public synchronized boolean add(Object obj) {
        append((L2ListElem) obj);
        return true;
    }
    public synchronized boolean remove(Object o) {
        remove((L2ListElem) o);
        return true;
    }
    public int size() {
        return nElems;
    }
    public synchronized boolean contains(Object o) {
        for (L2ListElem e = next; e != this; e = e.next) {
            if (e.equals(o)) {
                return true;
            }
        }
        return false;
    }

    class L2ListIterator implements PersistentIterator, Iterator {
        private L2ListElem curr;
        private int counter;

        L2ListIterator() {
            curr = L2List.this;
            counter = updateCounter;
        }

        public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            curr = curr.next;
            return curr;
        }

        public int nextOid() {
            if (!hasNext()) {
                return 0;
            }
            curr = curr.next;
            return curr.getOid();
        }

        public boolean hasNext() {
            if (counter != updateCounter) {
                throw new IllegalStateException();
            }
            return curr.next != L2List.this;
        }

        public void remove() {
            if (counter != updateCounter || curr == L2List.this) {
                throw new IllegalStateException();
            }
            L2List.this.remove(curr);
            counter = updateCounter;
            curr = curr.prev;
        }
    }

    public synchronized Iterator iterator() {
        return new L2ListIterator();
    }
    public synchronized Object[] toArray() {
        L2ListElem[] arr = new L2ListElem[nElems];
        L2ListElem e = this;
        for (int i = 0; i < arr.length; i++) {
            arr[i] = e = e.next;
        }
        return arr;
    }
    public synchronized Object[] toArray(Object a[]) {
        int size = nElems;
        if (a.length < size) {
            a = (Object[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        }
        L2ListElem e = this;
        for (int i = 0; i < size; i++) {
            a[i] = e = e.next;
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
    public synchronized boolean containsAll(Collection c) {
        Iterator e = c.iterator();
        while (e.hasNext()) {
            if (!contains(e.next())) {
                return false;
            }
        }
        return true;
    }
    public synchronized boolean addAll(Collection c) {
        Iterator e = c.iterator();
        while (e.hasNext()) {
            add(e.next());
        }
        return true;
    }
    public synchronized boolean removeAll(Collection c) {
        boolean modified = false;
        Iterator e = iterator();
        while (e.hasNext()) {
            if (c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }
    public synchronized boolean retainAll(Collection c) {
        boolean modified = false;
        Iterator e = iterator();
        while (e.hasNext()) {
            if (!c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }
    public IterableIterator select(Class cls, String predicate) {
        Query query = new QueryImpl(getStorage());
        return query.select(cls, iterator(), predicate);
    }
}