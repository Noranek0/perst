package org.garret.perst;

public interface IPersistent extends ILoadable, IStoreable, java.io.Externalizable {
    public void load();

    public boolean isRaw();

    public boolean isModified();

    public boolean isDeleted();

    public boolean isPersistent();

    public void makePersistent(Storage storage);

    public void store();

    public void modify();

    public void loadAndModify();

    public int getOid();

    public void deallocate();

    public boolean recursiveLoading();

    public Storage getStorage();

    public void invalidate();

    public void assignOid(Storage storage, int oid, boolean raw);

    public void unassignOid();
}
