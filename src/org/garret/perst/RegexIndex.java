package org.garret.perst;

import java.util.*;

public interface RegexIndex<T> extends FieldIndex<T>
{
    public IterableIterator<T> match(String regex);
}
