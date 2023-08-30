package org.garret.perst;

public class RectangleR2 implements IValue, Cloneable {
    private double top;
    private double left;
    private double bottom;
    private double right;

    /**
     * Smallest Y coordinate of the rectangle
     */
    public final double getTop() {
        return top;
    }
    public final double getLeft() {
        return left;
    }
    public final double getBottom() {
        return bottom;
    }
    public final double getRight() {
        return right;
    }
    public final double area() {
        return (bottom - top) * (right - left);
    }
    public static double joinArea(RectangleR2 a, RectangleR2 b) {
        double left = (a.left < b.left) ? a.left : b.left;
        double right = (a.right > b.right) ? a.right : b.right;
        double top = (a.top < b.top) ? a.top : b.top;
        double bottom = (a.bottom > b.bottom) ? a.bottom : b.bottom;
        return (bottom - top) * (right - left);
    }
    public double distance(double x, double y) {
        if (x >= left && x <= right) {
            if (y >= top) {
                if (y <= bottom) {
                    return 0;
                } else {
                    return y - bottom;
                }
            } else {
                return top - y;
            }
        } else if (y >= top && y <= bottom) {
            if (x < left) {
                return left - x;
            } else {
                return x - right;
            }
        }
        double dx = x < left ? left - x : x - right;
        double dy = y < top ? top - y : y - bottom;
        return Math.sqrt(dx * dx + dy * dy);
    }
    public Object clone() {
        try {
            RectangleR2 r = (RectangleR2) super.clone();
            r.top = this.top;
            r.left = this.left;
            r.bottom = this.bottom;
            r.right = this.right;
            return r;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    public RectangleR2(RectangleR2 r) {
        this.top = r.top;
        this.left = r.left;
        this.bottom = r.bottom;
        this.right = r.right;
    }
    public RectangleR2(double top, double left, double bottom, double right) {
        Assert.that(top <= bottom && left <= right);
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }
    public RectangleR2() {
    }

    public final void join(RectangleR2 r) {
        if (left > r.left) {
            left = r.left;
        }
        if (right < r.right) {
            right = r.right;
        }
        if (top > r.top) {
            top = r.top;
        }
        if (bottom < r.bottom) {
            bottom = r.bottom;
        }
    }
    public static RectangleR2 join(RectangleR2 a, RectangleR2 b) {
        RectangleR2 r = new RectangleR2(a);
        r.join(b);
        return r;
    }
    public final boolean intersects(RectangleR2 r) {
        return left <= r.right && top <= r.bottom && right >= r.left && bottom >= r.top;
    }
    public final boolean contains(RectangleR2 r) {
        return left <= r.left && top <= r.top && right >= r.right && bottom >= r.bottom;
    }
    public boolean equals(Object o) {
        if (o instanceof RectangleR2) {
            RectangleR2 r = (RectangleR2) o;
            return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
        }
        return false;
    }
    public int hashCode() {
        return (int) (Double.doubleToLongBits(top) ^ (Double.doubleToLongBits(bottom) << 1)
                ^ (Double.doubleToLongBits(left) << 2) ^ (Double.doubleToLongBits(right) << 3));
    }
    public String toString() {
        return "top=" + top + ", left=" + left + ", bottom=" + bottom + ", right=" + right;
    }
}
