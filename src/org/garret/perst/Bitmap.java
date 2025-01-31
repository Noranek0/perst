package org.garret.perst;

import java.util.*;

public class Bitmap implements Iterable {
    class BitmapIterator implements Iterator, PersistentIterator {
        public boolean hasNext() {
            return curr < n_bits;
        }

        public Object next() {
            int i = curr, n = n_bits;
            if (i >= n) {
                throw new NoSuchElementException();
            }
            int[] bm = bitmap;
            while (++i < n && (bm[i >>> 5] & (1 << (i & 31))) == 0)
                ;
            Object obj = storage.getObjectByOID(curr);
            prev = curr;
            curr = i;
            return obj;
        }

        public int nextOid() {
            int i = curr, n = n_bits;
            if (i >= n) {
                throw new NoSuchElementException();
            }
            int[] bm = bitmap;
            while (++i < n && (bm[i >>> 5] & (1 << (i & 31))) == 0)
                ;
            prev = curr;
            curr = i;
            return prev;
        }

        public void remove() {
            if (prev < 0) {
                throw new NoSuchElementException();
            }
            bitmap[prev >>> 5] &= ~(1 << (prev & 31));
        }

        BitmapIterator() {
            int[] bm = bitmap;
            int i, n;
            for (i = 0, n = n_bits; i < n && (bm[i >>> 5] & (1 << (i & 31))) == 0; i++)
                ;
            curr = i;
            prev = -1;
        }

        int curr;
        int prev;
    };

    public boolean contains(int oid) {
        return oid < n_bits && (bitmap[oid >>> 5] & (1 << (oid & 31))) != 0;
    }

    public Iterator iterator() {
        return new BitmapIterator();
    }

    public void and(Bitmap other) {
        int[] b1 = bitmap;
        int[] b2 = other.bitmap;
        int len = b1.length < b2.length ? b1.length : b2.length;
        int i;
        for (i = 0; i < len; i++) {
            b1[i] &= b2[i];
        }
        while (i < b1.length) {
            b1[i++] = 0;
        }
        if (n_bits > other.n_bits) {
            n_bits = other.n_bits;
        }
    }

    public void or(Bitmap other) {
        int[] b1 = bitmap;
        int[] b2 = other.bitmap;
        if (b1.length < b2.length) {
            bitmap = new int[b2.length];
            System.arraycopy(b1, 0, bitmap, 0, b1.length);
            b1 = bitmap;
        }
        int len = b1.length < b2.length ? b1.length : b2.length;
        for (int i = 0; i < len; i++) {
            b1[i] |= b2[i];
        }
        if (n_bits < other.n_bits) {
            n_bits = other.n_bits;
        }
    }

    public void xor(Bitmap other) {
        int[] b1 = bitmap;
        int[] b2 = other.bitmap;
        if (b1.length < b2.length) {
            bitmap = new int[b2.length];
            System.arraycopy(b1, 0, bitmap, 0, b1.length);
            b1 = bitmap;
        }
        int len = b1.length < b2.length ? b1.length : b2.length;
        for (int i = 0; i < len; i++) {
            b1[i] ^= b2[i];
        }
        if (n_bits < other.n_bits) {
            n_bits = other.n_bits;
        }
    }

    public Bitmap(Storage sto, Iterator i) {
        storage = sto;
        n_bits = sto.getMaxOid();
        int[] bm = new int[(n_bits + 31) >>> 5];
        PersistentIterator pi = (PersistentIterator) i;
        int oid;
        while ((oid = pi.nextOid()) != 0) {
            bm[oid >>> 5] |= 1 << (oid & 31);
        }
        bitmap = bm;
    }

    Storage storage;
    int[] bitmap;
    int n_bits;
}