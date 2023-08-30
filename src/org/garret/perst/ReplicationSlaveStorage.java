package org.garret.perst;

public interface ReplicationSlaveStorage extends Storage {
    public boolean isConnected();
    public void waitForModification();
}
