package org.garret.perst;

public class Assert {
    public static final void that(boolean cond) {
        if (!cond) {
            throw new AssertionFailed();
        }
    }

    public static final void that(String description, boolean cond) {
        if (!cond) {
            throw new AssertionFailed(description);
        }
    }

    public static final void failed() {
        throw new AssertionFailed();
    }

    public static final void failed(String description) {
        throw new AssertionFailed(description);
    }
}
