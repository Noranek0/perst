package org.garret.perst;

public class PersistentString extends PersistentResource {
    public PersistentString(String str) {
        this.str = str;
    }

    private PersistentString() {
    }

    public String toString() {
        return str;
    }

    public void append(String tail) {
        modify();
        str = str + tail;
    }

    public void set(String str) {
        modify();
        this.str = str;
    }

    public String get() {
        return str;
    }

    private String str;
}
