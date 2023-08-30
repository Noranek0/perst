package org.garret.perst;

import org.garret.perst.impl.ClassDescriptor;

public class Key {
    public final int type;

    public final int ival;
    public final long lval;
    public final double dval;
    public final Object oval;

    public final int inclusion;

    public boolean equals(Object o) {
        if (o instanceof Key) {
            Key key = (Key) o;
            return key.type == type && key.ival == ival && key.lval == lval && key.dval == dval & key.oval == oval;
        }
        return false;
    }

    public Key(boolean v) {
        this(v, true);
    }
    public Key(byte v) {
        this(v, true);
    }
    public Key(char v) {
        this(v, true);
    }
    public Key(short v) {
        this(v, true);
    }
    public Key(int v) {
        this(v, true);
    }
    public Key(long v) {
        this(v, true);
    }
    public Key(float v) {
        this(v, true);
    }
    public Key(double v) {
        this(v, true);
    }
    public Key(java.util.Date v) {
        this(v, true);
    }
    public Key(String v) {
        this(v, true);
    }
    public Key(char[] v) {
        this(v, true);
    }
    public Key(byte[] v) {
        this(v, true);
    }
    public Key(IValue v) {
        this(v, true);
    }
    public Key(Enum v) {
        this(v, true);
    }
    public Key(Object[] v) {
        this(v, true);
    }
    public Key(Object v1, Object v2) {
        this(new Object[] { v1, v2 }, true);
    }
    public Key(Object v) {
        this(v, true);
    }
    public Key(IPersistent v) {
        this(v, true);
    }

    private Key(int type, long lval, double dval, Object oval, boolean inclusive) {
        this.type = type;
        this.ival = (int) lval;
        this.lval = lval;
        this.dval = dval;
        this.oval = oval;
        this.inclusion = inclusive ? 1 : 0;
    }
    public Key(boolean v, boolean inclusive) {
        this(ClassDescriptor.tpBoolean, v ? 1 : 0, 0.0, null, inclusive);
    }
    public Key(byte v, boolean inclusive) {
        this(ClassDescriptor.tpByte, v, 0.0, null, inclusive);
    }
    public Key(char v, boolean inclusive) {
        this(ClassDescriptor.tpChar, v, 0.0, null, inclusive);
    }
    public Key(short v, boolean inclusive) {
        this(ClassDescriptor.tpShort, v, 0.0, null, inclusive);
    }
    public Key(int v, boolean inclusive) {
        this(ClassDescriptor.tpInt, v, 0.0, null, inclusive);
    }
    public Key(long v, boolean inclusive) {
        this(ClassDescriptor.tpLong, v, 0.0, null, inclusive);
    }
    public Key(float v, boolean inclusive) {
        this(ClassDescriptor.tpFloat, 0, v, null, inclusive);
    }
    public Key(double v, boolean inclusive) {
        this(ClassDescriptor.tpDouble, 0, v, null, inclusive);
    }
    public Key(java.util.Date v, boolean inclusive) {
        this(ClassDescriptor.tpDate, v == null ? Storage.INVALID_DATE : v.getTime(), 0.0, null, inclusive);
    }
    public Key(String v, boolean inclusive) {
        this(ClassDescriptor.tpString, 0, 0.0, v, inclusive);
    }
    public Key(char[] v, boolean inclusive) {
        this(ClassDescriptor.tpString, 0, 0.0, v, inclusive);
    }
    public Key(IPersistent v, boolean inclusive) {
        this(ClassDescriptor.tpObject, v == null ? 0 : v.getOid(), 0.0, v, inclusive);
    }
    public Key(Object v, boolean inclusive) {
        this(ClassDescriptor.tpObject, 0, 0.0, v, inclusive);
    }
    public Key(Object v, int oid, boolean inclusive) {
        this(ClassDescriptor.tpObject, oid, 0.0, v, inclusive);
    }
    public Key(Object[] v, boolean inclusive) {
        this(ClassDescriptor.tpArrayOfObject, 0, 0.0, v, inclusive);
    }
    public Key(IValue v, boolean inclusive) {
        this(ClassDescriptor.tpValue, 0, 0.0, v, inclusive);
    }
    public Key(Enum v, boolean inclusive) {
        this(ClassDescriptor.tpEnum, v.ordinal(), 0.0, v, inclusive);
    }
    public Key(Object v1, Object v2, boolean inclusive) {
        this(new Object[] { v1, v2 }, inclusive);
    }
    public Key(byte[] v, boolean inclusive) {
        this(ClassDescriptor.tpArrayOfByte, 0, 0.0, v, inclusive);
    }
}
