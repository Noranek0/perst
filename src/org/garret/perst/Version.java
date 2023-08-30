package org.garret.perst;

import java.util.Date;

public class Version extends PersistentResource {
    public synchronized VersionHistory getVersionHistory() {
        return history;
    }

    public synchronized Version[] getPredecessors() {
        return predecessors.toArray(new Version[predecessors.size()]);
    }

    public synchronized Version[] getSuccessors() {
        return successors.toArray(new Version[successors.size()]);
    }

    public boolean isCheckedIn() {
        return id != null;
    }

    public boolean isCheckedOut() {
        return id == null;
    }

    public Version newVersion() {
        try {
            Version newVersion = (Version) clone();
            newVersion.predecessors = getStorage().<Version>createLink(1);
            newVersion.predecessors.add(this);
            newVersion.successors = getStorage().<Version>createLink(1);
            newVersion.labels = new String[0];
            newVersion.id = null;
            newVersion.oid = 0;
            newVersion.state = 0;
            return newVersion;
        } catch (CloneNotSupportedException x) {
            // Could not happen sense we clone ourselves
            throw new AssertionFailed("Clone not supported");
        }
    }

    public void checkin() {
        synchronized (history) {
            Assert.that(isCheckedOut());
            for (int i = 0; i < predecessors.size(); i++) {
                Version predecessor = predecessors.get(i);
                synchronized (predecessor) {
                    if (i == 0) {
                        id = predecessor.constructId();
                    }
                    predecessor.successors.add(this);
                }
            }
            date = new Date();
            history.versions.add(this);
            history.current = this;
            modify();
        }
    }

    public void addPredecessor(Version predecessor) {
        synchronized (predecessor) {
            synchronized (this) {
                predecessors.add(predecessor);
                if (isCheckedIn()) {
                    predecessor.successors.add(this);
                }
            }
        }
    }

    public Date getDate() {
        return date;
    }

    public synchronized String[] getLabels() {
        return labels;
    }

    public synchronized void addLabel(String label) {
        String[] newLabels = new String[labels.length + 1];
        System.arraycopy(labels, 0, newLabels, 0, labels.length);
        newLabels[newLabels.length - 1] = label;
        labels = newLabels;
        modify();
    }

    public synchronized boolean hasLabel(String label) {
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].equals(label)) {
                return true;
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    protected Version(Storage storage) {
        super(storage);
        successors = storage.createLink(1);
        predecessors = storage.createLink(1);
        labels = new String[0];
        date = new Date();
        id = "1";
    }

    private Version() {
    }

    private String constructId() {
        int suffixPos = id.lastIndexOf('.');
        int suffix = Integer.parseInt(id.substring(suffixPos + 1));
        String nextId = suffixPos < 0
                ? Integer.toString(suffix + 1)
                : id.substring(0, suffixPos) + Integer.toString(suffix + 1);
        if (successors.size() != 0) {
            nextId += '.' + successors.size() + ".1";
        }
        return nextId;
    }

    private Link<Version> successors;
    private Link<Version> predecessors;
    private String[] labels;
    private Date date;
    private String id;
    VersionHistory history;
}
