package org.garret.perst.fulltext;

public class FullTextQuery 
{
    public static final int MATCH = 0;     
    public static final int STRICT_MATCH = 1;     
    public static final int AND   = 2; 
    public static final int NEAR  = 3; 
    public static final int OR    = 4; 
    public static final int NOT   = 5; 

    public static final String[] operatorName = { "MATCH", "STRICT_MATCH", "AND", "NEAR", "OR", "NOT" };

    public int op;

    public void visit(FullTextQueryVisitor visitor) { 
        visitor.visit(this);
    }

    public boolean isConstrained() { 
        return false;
    }

    public FullTextQuery(int op) { 
        this.op = op;
    }       
}


