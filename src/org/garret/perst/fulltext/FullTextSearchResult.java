package org.garret.perst.fulltext;

import java.util.*;

public class FullTextSearchResult {
    public int estimation;
    public FullTextSearchHit[] hits;

    public FullTextSearchResult merge(FullTextSearchResult another) {
        if (hits.length == 0 || another.hits.length == 0) {
            return new FullTextSearchResult(new FullTextSearchHit[0], 0);
        }
        FullTextSearchHit[] joinHits = new FullTextSearchHit[hits.length + another.hits.length];
        System.arraycopy(hits, 0, joinHits, 0, hits.length);
        System.arraycopy(another.hits, 0, joinHits, hits.length, another.hits.length);
        Arrays.sort(joinHits, new Comparator() { 
            public int compare(Object o1, Object o2) {
                return ((FullTextSearchHit)o1).oid - ((FullTextSearchHit)o2).oid;
            }
        });
        int n = 0;
        for (int i = 1; i < joinHits.length; i++) { 
            if (joinHits[i].oid == joinHits[i-1].oid) {                    
                joinHits[n++] = new FullTextSearchHit(joinHits[i].storage, joinHits[i].oid, joinHits[i-1].rank + joinHits[i].rank);                    
                i += 1;
            } 
        }
        FullTextSearchHit[] mergeHits = new FullTextSearchHit[n];
        System.arraycopy(joinHits, 0, mergeHits, 0, n);
        Arrays.sort(joinHits);
        return new FullTextSearchResult(joinHits, Math.min(estimation*n/hits.length, another.estimation*n/another.hits.length));
    }

    public FullTextSearchResult(FullTextSearchHit[] hits, int estimation) { 
        this.hits = hits;
        this.estimation = estimation;
    }
}
