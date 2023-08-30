package org.garret.perst.fulltext;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FullTextIndexable {
}