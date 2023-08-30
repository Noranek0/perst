package org.garret.perst;

public class MemoryUsage {
    public Class cls;
    public int nInstances;
    public long totalSize;
    public long allocatedSize;

    public MemoryUsage(Class cls) {
        this.cls = cls;
    }
}
