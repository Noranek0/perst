package org.garret.perst;

public interface CustomAllocator extends IPersistent {
    long getSegmentBase();
    long getSegmentSize();

    long allocate(long size);

    long reallocate(long pos, long oldSize, long newSize);

    void free(long pos, long size);

    void commit();

    public long getUsedMemory();

    public long getAllocatedMemory();
}