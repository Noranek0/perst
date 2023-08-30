package org.garret.perst;

public abstract class Relation<M, O> extends Persistent implements Link<M> {
    public O getOwner() { 
        return owner;
    }

    public void setOwner(O owner) { 
        this.owner = owner;
        modify();
    }

    public Relation(O owner) {
        this.owner = owner;
    }
    
    protected Relation() {}

    private O owner;
}
