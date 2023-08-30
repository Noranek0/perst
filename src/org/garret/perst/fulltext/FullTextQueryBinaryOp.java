package org.garret.perst.fulltext;

public class FullTextQueryBinaryOp extends FullTextQuery
{
    public FullTextQuery left;
    public FullTextQuery right;
    
    public void visit(FullTextQueryVisitor visitor) { 
        visitor.visit(this);
        left.visit(visitor);
        right.visit(visitor);
    }

    public boolean isConstrained() { 
        return op == OR 
            ? left.isConstrained() && right.isConstrained()
            : left.isConstrained() || right.isConstrained();
    }

    public String toString() {
        return op == OR
            ? '(' + left.toString() + ") OR (" + right.toString() + ')'
            : left.toString() + ' ' + operatorName[op] + ' ' + right.toString();
    }

    public FullTextQueryBinaryOp(int op, FullTextQuery left, FullTextQuery right) { 
        super(op);
        this.left = left;
        this.right = right;
    }
}    
