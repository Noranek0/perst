package org.garret.perst;

public interface RandomAccessStream {
    public long setPosition(long pos);
    public long getPosition();
    public long size();
}