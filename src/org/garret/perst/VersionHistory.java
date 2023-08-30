package org.garret.perst;

import java.util.*;

public class VersionHistory<V extends Version> extends PersistentResource {
    public synchronized V getCurrent() {
        return current;
    }

    public synchronized void setCurrent(V version) {
        current = version;
        modify();
    }

    public synchronized V checkout() {
        Assert.that(current.isCheckedIn());
        return (V) current.newVersion();
    }

    public synchronized V getRoot() {
        return versions.get(0);
    }

    public synchronized V getLatestBefore(Date timestamp) {
        if (timestamp == null) {
            return versions.get(versions.size() - 1);
        }
        int l = 0, n = versions.size(), r = n;
        long t = timestamp.getTime() + 1;
        while (l < r) {
            int m = (l + r) >> 1;
            if (versions.get(m).getDate().getTime() < t) {
                l = m + 1;
            } else {
                r = m;
            }
        }
        return r > 0 ? versions.get(r - 1) : null;
    }

    public synchronized V getEarliestAfter(Date timestamp) {
        if (timestamp == null) {
            return versions.get(0);
        }
        int l = 0, n = versions.size(), r = n;
        long t = timestamp.getTime();
        while (l < r) {
            int m = (l + r) >> 1;
            if (versions.get(m).getDate().getTime() < t) {
                l = m + 1;
            } else {
                r = m;
            }
        }
        return r < n ? versions.get(r) : null;
    }

    public synchronized V getVersionByLabel(String label) {
        for (int i = versions.size(); --i >= 0;) {
            V v = versions.get(i);
            if (v.hasLabel(label)) {
                return v;
            }
        }
        return null;
    }

    public synchronized V getVersionById(String id) {
        for (int i = versions.size(); --i >= 0;) {
            V v = versions.get(i);
            if (v.getId().equals(id)) {
                return v;
            }
        }
        return null;
    }

    public synchronized Version[] getAllVersions() {
        return versions.toArray(new Version[versions.size()]);
    }

    public synchronized Iterator<V> iterator() {
        return versions.iterator();
    }

    public VersionHistory(V root) {
        versions = root.getStorage().<V>createLink(1);
        versions.add(root);
        current = root;
        current.history = this;
    }

    Link<V> versions;
    V current;
}