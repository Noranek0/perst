package org.garret.perst;

public abstract class MultidimensionalComparator<T> extends Persistent {
    public static final int LEFT_UNDEFINED = -2;
    public static final int LT = -1;
    public static final int EQ = 0;
    public static final int GT = 1;
    public static final int RIGHT_UNDEFINED = 2;
    public static final int NE = 3;

    public abstract int compare(T m1, T m2, int i);
    public abstract int getNumberOfDimensions();
    public abstract T cloneField(T obj, int i);

    protected MultidimensionalComparator(Storage storage) {
        super(storage);
    }
    protected MultidimensionalComparator() {
    }
}
