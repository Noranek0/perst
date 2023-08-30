package org.garret.perst;

public class L2ListElem extends PersistentResource {
    protected L2ListElem next;
    protected L2ListElem prev;

    public L2ListElem() {
        next = prev = this;
    }

    public L2ListElem getNext() {
        return next;
    }
    public L2ListElem getPrev() {
        return prev;
    }
    public void prune() {
        modify();
        next = prev = this;
    }
    public void linkAfter(L2ListElem elem) {
        modify();
        next.modify();
        elem.modify();
        elem.next = next;
        elem.prev = this;
        next.prev = elem;
        next = elem;
    }
    public void linkBefore(L2ListElem elem) {
        modify();
        prev.modify();
        elem.modify();
        elem.next = this;
        elem.prev = prev;
        prev.next = elem;
        prev = elem;
    }
    public void unlink() {
        next.modify();
        prev.modify();
        next.prev = prev;
        prev.next = next;
    }
}