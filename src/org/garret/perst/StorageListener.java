package org.garret.perst;

public abstract class StorageListener {
    public void databaseCorrupted() {
    }
    public void recoveryCompleted() {
    }
    public void onObjectLoad(Object obj) {
    }
    public void onObjectStore(Object obj) {
    }
    public void onObjectDelete(Object obj) {
    }
    public void onObjectAssignOid(Object obj) {
    }
    public void onMasterDatabaseUpdate() {
    }
    public void onTransactionCommit() {
    }
    public void onTransactionRollback() {
    }
    public void gcStarted() {
    }
    public void deallocateObject(Class cls, int oid) {
    }
    public void gcCompleted(int nDeallocatedObjects) {
    }
    public boolean onXmlExportError(int oid, Exception x) {
        return true;
    }
    public boolean replicationError(String host) {
        return false;
    }
    public void JSQLRuntimeError(JSQLRuntimeException x) {
    }
    public void queryExecution(Class table, String query, long elapsedTime, boolean sequentialSearch) {
    }
    public void sequentialSearchPerformed(Class table, String query) {
    }
    public void sortResultSetPerformed(Class table, String query) {
    }
    public boolean objectNotExported(int oid, StorageError x) {
        return false;
    }
    public void indexCreated(Class table, String field) {
    }
}
