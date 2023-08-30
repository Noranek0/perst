package org.garret.perst.fulltext;

public class FullTextQueryMatchOp extends FullTextQuery
{
    public String word;
    public int    pos;
    public int    wno;

    public void visit(FullTextQueryVisitor visitor) { 
        visitor.visit(this);
    }

    public boolean isConstrained() { 
        return true;
    }

    public String toString() { 
        return op == MATCH ? word : '"' + word + '"';
    }

    public FullTextQueryMatchOp(int op, String word, int pos) { 
        super(op);
        this.word = word;
        this.pos = pos;
    }
}    
