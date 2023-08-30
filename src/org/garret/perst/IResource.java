package org.garret.perst;

public interface IResource {
    public void sharedLock();
    public boolean sharedLock(long timeout);
    public void exclusiveLock();
    public boolean exclusiveLock(long timeout);
    public void unlock();
    public void reset();
}
