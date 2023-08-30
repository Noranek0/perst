package org.garret.perst;

import java.util.Iterator;
import java.util.Collection;

public interface Query<T> extends Iterable<T> {
    public IterableIterator<T> select(Class cls, Iterator<T> iterator, String predicate) throws CompileError;
    public IterableIterator<T> select(String className, Iterator<T> iterator, String predicate) throws CompileError;
    public void setParameter(int index, Object value);
    public void setIntParameter(int index, long value);
    public void setRealParameter(int index, double value);
    public void setBoolParameter(int index, boolean value);
    public void prepare(Class cls, String predicate);
    public void prepare(String className, String predicate);
    public IterableIterator<T> execute(Iterator<T> iterator);
    public IterableIterator<T> execute();

    public void enableRuntimeErrorReporting(boolean enabled);
    public void setResolver(Class original, Class resolved, Resolver resolver);
    public void addIndex(String key, GenericIndex<T> index);
    public void setIndexProvider(IndexProvider indexProvider);
    public void setClass(Class cls);

    enum ClassExtentLockType {
        None,
        Shared,
        Exclusive
    };

    public void setClassExtent(Collection<T> set, ClassExtentLockType lock);
    public CodeGenerator getCodeGenerator(Class cls);
    public CodeGenerator getCodeGenerator();
}
