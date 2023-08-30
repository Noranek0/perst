package org.garret.perst.fulltext;

public class Occurrence implements Comparable 
{
    public String word;
    public int    position;
    public int    kind;

    public Occurrence(String word, int position, int kind) {
        this.word = word;
        this.position = position;
        this.kind = kind;
    }

    public int compareTo(Object o) { 
        Occurrence occ = (Occurrence)o;
        int diff = word.compareTo(occ.word);
        if (diff == 0) { 
            diff = position - occ.position;
        }
        return diff;
    }
}