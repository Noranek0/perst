package org.garret.perst;

import java.util.*;
import java.lang.reflect.Field;

public class Projection<From, To> extends HashSet<To> {
    public Projection(Class type, String fieldName) {
        setProjectionField(type, fieldName);
    }

    public Projection() {
    }

    public void setProjectionField(Class type, String fieldName) {
        try {
            field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (Exception x) {
            throw new StorageError(StorageError.KEY_NOT_FOUND, x);
        }
    }

    public void project(From[] selection) {
        for (int i = 0; i < selection.length; i++) {
            map(selection[i]);
        }
    }

    public void project(From obj) {
        map(obj);
    }

    public void project(Iterator<From> selection) {
        while (selection.hasNext()) {
            map(selection.next());
        }
    }

    public void project(Collection<From> c) {
        for (From o : c) {
            map(o);
        }
    }

    public void join(Projection<From, To> prj) {
        retainAll(prj);
    }

    public void reset() {
        clear();
    }

    public boolean add(To obj) {
        if (obj != null) {
            return super.add(obj);
        }
        return false;
    }

    protected void map(From obj) {
        if (field == null) {
            add((To) obj);
        } else {
            try {
                Object o = field.get(obj);
                if (o instanceof Link) {
                    Object[] arr = ((Link) o).toArray();
                    for (int i = 0; i < arr.length; i++) {
                        add((To) arr[i]);
                    }
                } else if (o instanceof Object[]) {
                    Object[] arr = (Object[]) o;
                    for (int i = 0; i < arr.length; i++) {
                        add((To) arr[i]);
                    }
                } else {
                    add((To) o);
                }
            } catch (Exception x) {
                throw new StorageError(StorageError.ACCESS_VIOLATION, x);
            }
        }
    }

    private Field field;
}
