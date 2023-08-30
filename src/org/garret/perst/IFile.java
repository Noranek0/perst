package org.garret.perst;

public interface IFile {
    void write(long pos, byte[] buf);
    int read(long pos, byte[] buf);
    void sync();
    boolean tryLock(boolean shared);
    void lock(boolean shared);
    void unlock();
    void close();
    long length();
}
