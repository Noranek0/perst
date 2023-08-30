package org.garret.perst;

public class NullFile implements IFile {
    public void write(long pos, byte[] buf) {
    }

    public int read(long pos, byte[] buf) {
        return 0;
    }

    public void sync() {
    }

    public boolean tryLock(boolean shared) {
        return true;
    }

    public void lock(boolean shared) {
    }

    public void unlock() {
    }

    public void close() {
    }

    public long length() {
        return 0;
    }
}
