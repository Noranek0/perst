package org.garret.perst.fulltext;

public class FullTextQueryUnaryOp extends FullTextQuery
{
    public FullTextQuery opd;

    public void visit(FullTextQueryVisitor visitor) { 
        visitor.visit(this);
        opd.visit(visitor);
    }

    public boolean isConstrained() { 
        return op == NOT ? false : opd.isConstrained();
    }

    public String toString() { 
        return operatorName[op] + '(' + opd.toString() + ')';
    }

    public FullTextQueryUnaryOp(int op, FullTextQuery opd) { 
        super(op);
        this.opd = opd;
    }
}    
