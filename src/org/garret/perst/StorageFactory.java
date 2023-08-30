package org.garret.perst;
import org.garret.perst.impl.*;

public class StorageFactory {
    public Storage createStorage() {
        return new StorageImpl();
    }
    public ReplicationMasterStorage createReplicationMasterStorage(int port, String[] replicationSlaveNodes, int asyncBufSize) {
        return new ReplicationMasterStorageImpl(port, replicationSlaveNodes, asyncBufSize, null);
    }
    public ReplicationMasterStorage createReplicationMasterStorage(int port, String[] replicationSlaveNodes, int asyncBufSize, String pageTimestampFile) {
        return new ReplicationMasterStorageImpl(port, replicationSlaveNodes, asyncBufSize, pageTimestampFile);
    }
    public ReplicationSlaveStorage createReplicationSlaveStorage(int slavePort) {
        return new ReplicationStaticSlaveStorageImpl(slavePort, null);
    }
    public ReplicationSlaveStorage createReplicationSlaveStorage(int slavePort, String pageTimestampFile) {
        return new ReplicationStaticSlaveStorageImpl(slavePort, pageTimestampFile);
    }
    public ReplicationSlaveStorage addReplicationSlaveStorage(String replicationMasterNode, int masterPort) {
        return new ReplicationDynamicSlaveStorageImpl(replicationMasterNode, masterPort, null);
    }
    public ReplicationSlaveStorage addReplicationSlaveStorage(String replicationMasterNode, int masterPort, String pageTimestampFile) {
        return new ReplicationDynamicSlaveStorageImpl(replicationMasterNode, masterPort, pageTimestampFile);
    }
    public static StorageFactory getInstance() { 
        return instance;
    }

    protected static final StorageFactory instance = new StorageFactory();
};
