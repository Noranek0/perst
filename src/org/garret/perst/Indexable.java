package org.garret.perst;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexable {
    boolean unique() default false;
    boolean thick() default false;
    boolean caseInsensitive() default false;
    boolean randomAccess() default false;
    boolean regex() default false;
    boolean autoincrement() default false;
}