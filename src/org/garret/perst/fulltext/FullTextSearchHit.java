package org.garret.perst.fulltext;

import org.garret.perst.*;

public class FullTextSearchHit implements Comparable { 
    public Object getDocument() { 
        return storage.getObjectByOID(oid);
    }

    public final float rank;
    public final int oid;

    public final Storage storage;

    public int compareTo(Object o) {
        float oRank = ((FullTextSearchHit)o).rank;
        return rank > oRank ? -1 : rank < oRank ? 1 : 0;
    }

    public FullTextSearchHit(Storage storage, int oid, float rank) { 
        this.storage = storage;
        this.oid = oid;
        this.rank = rank;
    }    
}
