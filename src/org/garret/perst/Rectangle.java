package org.garret.perst;

public class Rectangle implements IValue, Cloneable {
    private int top;
    private int left;
    private int bottom;
    private int right;

    public final int getTop() {
        return top;
    }
    public final int getLeft() {
        return left;
    }
    public final int getBottom() {
        return bottom;
    }
    public final int getRight() {
        return right;
    }
    public final long area() {
        return (long) (bottom - top) * (right - left);
    }
    public static long joinArea(Rectangle a, Rectangle b) {
        int left = (a.left < b.left) ? a.left : b.left;
        int right = (a.right > b.right) ? a.right : b.right;
        int top = (a.top < b.top) ? a.top : b.top;
        int bottom = (a.bottom > b.bottom) ? a.bottom : b.bottom;
        return (long) (bottom - top) * (right - left);
    }
    public double distance(int x, int y) {
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
        int dx = x < left ? left - x : x - right;
        int dy = y < top ? top - y : y - bottom;
        return Math.sqrt((double) dx * dx + (double) dy * dy);
    }

    public Object clone() {
        try {
            Rectangle r = (Rectangle) super.clone();
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

    public Rectangle(Rectangle r) {
        this.top = r.top;
        this.left = r.left;
        this.bottom = r.bottom;
        this.right = r.right;
    }
    public Rectangle(int top, int left, int bottom, int right) {
        Assert.that(top <= bottom && left <= right);
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }
    public Rectangle() {
    }

    public final void join(Rectangle r) {
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
    public static Rectangle join(Rectangle a, Rectangle b) {
        Rectangle r = new Rectangle(a);
        r.join(b);
        return r;
    }
    public final boolean intersects(Rectangle r) {
        return left <= r.right && top <= r.bottom && right >= r.left && bottom >= r.top;
    }
    public final boolean contains(Rectangle r) {
        return left <= r.left && top <= r.top && right >= r.right && bottom >= r.bottom;
    }
    public boolean equals(Object o) {
        if (o instanceof Rectangle) {
            Rectangle r = (Rectangle) o;
            return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
        }
        return false;
    }

    public int hashCode() {
        return top ^ (bottom << 1) ^ (left << 2) ^ (right << 3);
    }
    public String toString() {
        return "top=" + top + ", left=" + left + ", bottom=" + bottom + ", right=" + right;
    }
}
