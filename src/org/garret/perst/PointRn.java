package org.garret.perst;

public class PointRn implements IValue, Cloneable {
    double[] coords;

    public final double getCoord(int i) {
        return coords[i];
    }

    public PointRn(double[] coords) {
        this.coords = new double[coords.length];
        System.arraycopy(coords, 0, this.coords, 0, coords.length);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append('(');
        buf.append(coords[0]);
        for (int i = 1; i < coords.length; i++) {
            buf.append(',');
            buf.append(coords[i]);
        }
        buf.append(')');
        return buf.toString();
    }
}
