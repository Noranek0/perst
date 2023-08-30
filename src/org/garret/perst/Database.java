package org.garret.perst;

import java.util.*;
import java.io.StringReader;
import java.lang.reflect.*;
import org.garret.perst.fulltext.*;
import org.garret.perst.impl.ClassDescriptor;

public class Database implements IndexProvider {
    public Database(Storage storage, boolean multithreaded) {
        this(storage, multithreaded, true, null);
    }

    public Database(Storage storage, boolean multithreaded, boolean autoRegisterTables, FullTextSearchHelper helper) {
        this.storage = storage;
        this.multithreaded = multithreaded;
        this.autoRegisterTables = autoRegisterTables;
        this.searchBaseClasses = !autoRegisterTables
                && !Boolean.FALSE.equals(storage.getProperty("perst.search.base.classes"));
        this.globalClassExtent = !Boolean.FALSE.equals(storage.getProperty("perst.global.class.extent"));
        if (multithreaded) {
            storage.setProperty("perst.alternative.btree", Boolean.TRUE);
        }
        storage.setProperty("perst.concurrent.iterator", Boolean.TRUE);
        Object root = storage.getRoot();
        boolean schemaUpdated = false;
        if (root instanceof Index) { // backward compatibility
            beginTransaction();
            metadata = new Metadata(storage, (Index) root, helper);
            storage.setRoot(metadata);
            schemaUpdated = true;
        } else if (root == null) {
            beginTransaction();
            metadata = new Metadata(storage, helper);
            storage.setRoot(metadata);
            schemaUpdated = true;
        } else {
            metadata = (Metadata) root;
        }
        schemaUpdated |= reloadSchema();
        if (schemaUpdated) {
            commitTransaction();
        }
    }

    public Database(Storage storage) {
        this(storage, false);
    }

    private boolean reloadSchema() {
        boolean schemaUpdated = false;
        metadata = (Metadata) storage.getRoot();
        Iterator iterator = metadata.metaclasses.entryIterator();
        tables = new HashMap<Class, Table>();
        while (iterator.hasNext()) {
            Map.Entry map = (Map.Entry) iterator.next();
            Table table = (Table) map.getValue();
            Class cls = ClassDescriptor.loadClass(storage, (String) map.getKey());
            table.setClass(cls);
            tables.put(cls, table);
            schemaUpdated |= addIndices(table, cls);
        }
        return schemaUpdated;
    }

    public boolean isMultithreaded() {
        return multithreaded;
    }

    public boolean enableAutoIndices(boolean enabled) {
        boolean prev = autoIndices;
        autoIndices = enabled;
        return prev;
    }

    public void beginTransaction() {
        if (multithreaded) {
            storage.beginSerializableTransaction();
        }
    }

    public void commitTransaction() {
        if (multithreaded) {
            storage.commitSerializableTransaction();
        } else {
            storage.commit();
        }
    }

    public void rollbackTransaction() {
        if (multithreaded) {
            storage.rollbackSerializableTransaction();
        } else {
            storage.rollback();
        }
        reloadSchema();
    }

    private void checkTransaction() {
        if (!storage.isInsideThreadTransaction()) {
            throw new StorageError(StorageError.NOT_IN_TRANSACTION);
        }
    }

    public boolean createTable(Class table) {
        if (multithreaded) {
            checkTransaction();
            metadata.exclusiveLock();
        }
        if (tables.get(table) == null) {
            Table t = new Table();
            t.extent = storage.createSet();
            t.indices = storage.createLink();
            t.indicesMap = new HashMap();
            t.setClass(table);
            tables.put(table, t);
            metadata.metaclasses.put(table.getName(), t);
            addIndices(t, table);
            return true;
        }
        return false;
    }

    private boolean addIndices(Table table, Class cls) {
        boolean schemaUpdated = false;
        for (Field f : cls.getDeclaredFields()) {
            Indexable idx = (Indexable) f.getAnnotation(Indexable.class);
            if (idx != null) {
                int kind = INDEX_KIND_DEFAULT;
                if (idx.unique())
                    kind |= INDEX_KIND_UNIQUE;
                if (idx.caseInsensitive())
                    kind |= INDEX_KIND_CASE_INSENSITIVE;
                if (idx.thick())
                    kind |= INDEX_KIND_THICK;
                if (idx.randomAccess())
                    kind |= INDEX_KIND_RANDOM_ACCESS;
                if (idx.regex())
                    kind |= INDEX_KIND_REGEX;
                schemaUpdated |= createIndex(table, cls, f.getName(), kind);
                if (idx.autoincrement()) {
                    if (table.autoincrementIndex != null) {
                        throw new UnsupportedOperationException("Table can have only one autoincrement field");
                    }
                    table.autoincrementIndex = (FieldIndex) table.indicesMap.get(f.getName());
                }
            }
        }
        return schemaUpdated;
    }

    public boolean dropTable(Class table) {
        if (multithreaded) {
            checkTransaction();
            metadata.exclusiveLock();
        }
        Table t = tables.remove(table);
        if (t != null) {
            boolean savePolicy = storage.setRecursiveLoading(table, false);
            for (Object obj : t.extent) {
                if (obj instanceof FullTextSearchable || t.fullTextIndexableFields.size() != 0) {
                    metadata.fullTextIndex.delete(obj);
                }
                for (Class baseClass = table; (baseClass = baseClass.getSuperclass()) != null;) {
                    Table baseTable = (Table) tables.get(baseClass);
                    if (baseTable != null) {
                        if (multithreaded) {
                            baseTable.extent.exclusiveLock();
                        }
                        if (baseTable.extent.remove(obj)) {
                            Iterator iterator = baseTable.indicesMap.values().iterator();
                            while (iterator.hasNext()) {
                                FieldIndex index = (FieldIndex) iterator.next();
                                index.remove(obj);
                            }
                        }
                    }
                }
                storage.deallocate(obj);
            }
            metadata.metaclasses.remove(table.getName());
            t.deallocate();
            storage.setRecursiveLoading(table, savePolicy);
            return true;
        }
        return false;
    }

    public <T> boolean addRecord(T record) {
        return addRecord(record.getClass(), record);
    }

    private Table locateTable(Class cls, boolean exclusive) {
        return locateTable(cls, exclusive, true);
    }

    private Table locateTable(Class cls, boolean exclusive, boolean shouldExist) {
        Table table = null;
        if (multithreaded) {
            checkTransaction();
            metadata.sharedLock();
        }
        if (searchBaseClasses) {
            for (Class c = cls; c != null && (table = tables.get(c)) == null; c = c.getSuperclass())
                ;
        } else {
            table = tables.get(cls);
        }
        if (table == null) {
            if (shouldExist) {
                throw new StorageError(StorageError.CLASS_NOT_FOUND, cls.getName());
            }
            return null;
        }
        if (multithreaded) {
            if (exclusive) {
                table.extent.exclusiveLock();
            } else {
                table.extent.sharedLock();
            }
        }
        return table;
    }

    private void registerTable(Class cls) {
        if (multithreaded) {
            checkTransaction();
            metadata.sharedLock();
        }
        if (autoRegisterTables) {
            boolean exclusiveLockSet = false;
            for (Class c = cls; c != Object.class; c = c.getSuperclass()) {
                Table t = tables.get(c);
                if (t == null && c != PinnedPersistent.class && (globalClassExtent || c != Persistent.class)) {
                    if (multithreaded && !exclusiveLockSet) {
                        metadata.unlock(); // try to avoid deadlock caused by concurrent insertion of objects
                        exclusiveLockSet = true;
                    }
                    createTable(c);
                }
            }
        }
    }

    public <T> void updateFullTextIndex(T record) {
        if (multithreaded) {
            checkTransaction();
            metadata.fullTextIndex.exclusiveLock();
        }
        if (record instanceof FullTextSearchable) {
            metadata.fullTextIndex.add((FullTextSearchable) record);
        } else {
            StringBuffer fullText = new StringBuffer();
            for (Class c = record.getClass(); c != null; c = c.getSuperclass()) {
                Table t = tables.get(c);
                if (t != null) {
                    for (Field f : t.fullTextIndexableFields) {
                        Object text;
                        try {
                            text = f.get(record);
                        } catch (IllegalAccessException x) {
                            throw new IllegalAccessError();
                        }
                        if (text != null) {
                            fullText.append(' ');
                            fullText.append(text.toString());
                        }
                    }
                }
            }
            metadata.fullTextIndex.add(record, new StringReader(fullText.toString()), null);
        }
    }

    public <T> boolean addRecord(Class table, T record) {
        boolean added = false;
        boolean found = false;
        registerTable(table);
        ArrayList wasInsertedIn = new ArrayList();
        StringBuffer fullText = new StringBuffer();
        for (Class c = table; c != null; c = c.getSuperclass()) {
            Table t = tables.get(c);
            if (t != null) {
                found = true;
                if (multithreaded) {
                    t.extent.exclusiveLock();
                }
                if (t.extent.add(record)) {
                    wasInsertedIn.add(t.extent);
                    Iterator iterator = t.indicesMap.values().iterator();
                    while (iterator.hasNext()) {
                        FieldIndex index = (FieldIndex) iterator.next();
                        if (index == t.autoincrementIndex) {
                            index.append(record);
                            storage.modify(record);
                            wasInsertedIn.add(index);
                        } else if (index.put(record)) {
                            wasInsertedIn.add(index);
                        } else if (index.isUnique()) {
                            iterator = wasInsertedIn.iterator();
                            while (iterator.hasNext()) {
                                Object idx = iterator.next();
                                if (idx instanceof IPersistentSet) {
                                    ((IPersistentSet) idx).remove(record);
                                } else {
                                    ((FieldIndex) idx).remove(record);
                                }
                            }
                            return false;
                        }
                    }
                    for (Field f : t.fullTextIndexableFields) {
                        Object text;
                        try {
                            text = f.get(record);
                        } catch (IllegalAccessException x) {
                            throw new IllegalAccessError();
                        }
                        if (text != null) {
                            fullText.append(' ');
                            fullText.append(text.toString());
                        }
                    }
                    added = true;
                }
            }
        }
        if (!found) {
            throw new StorageError(StorageError.CLASS_NOT_FOUND, table.getName());
        }
        if (record instanceof FullTextSearchable || fullText.length() != 0) {
            if (multithreaded) {
                metadata.fullTextIndex.exclusiveLock();
            }
            if (record instanceof FullTextSearchable) {
                metadata.fullTextIndex.add((FullTextSearchable) record);
            } else {
                metadata.fullTextIndex.add(record, new StringReader(fullText.toString()), null);
            }
        }
        return added;
    }

    public <T> boolean deleteRecord(T record) {
        return deleteRecord(record.getClass(), record);
    }

    public <T> boolean deleteRecord(Class table, T record) {
        boolean removed = false;
        if (multithreaded) {
            checkTransaction();
            metadata.sharedLock();
        }
        boolean fullTextIndexed = false;
        for (Class c = table; c != null; c = c.getSuperclass()) {
            Table t = tables.get(c);
            if (t != null) {
                if (multithreaded) {
                    t.extent.exclusiveLock();
                }
                if (t.extent.remove(record)) {
                    Iterator iterator = t.indicesMap.values().iterator();
                    while (iterator.hasNext()) {
                        FieldIndex index = (FieldIndex) iterator.next();
                        index.remove(record);
                    }
                    if (t.fullTextIndexableFields.size() != 0) {
                        fullTextIndexed = true;
                    }
                    removed = true;
                }
            }
        }
        if (removed) {
            if (record instanceof FullTextSearchable || fullTextIndexed) {
                if (multithreaded) {
                    metadata.fullTextIndex.exclusiveLock();
                }
                metadata.fullTextIndex.delete(record);
            }
            storage.deallocate(record);
        }
        return removed;
    }

    public static final int INDEX_KIND_DEFAULT = 0;
    public static final int INDEX_KIND_UNIQUE = 1;
    public static final int INDEX_KIND_REGEX = 2;
    public static final int INDEX_KIND_THICK = 4;
    public static final int INDEX_KIND_RANDOM_ACCESS = 8;
    public static final int INDEX_KIND_CASE_INSENSITIVE = 16;

    public boolean createIndex(Class table, String key, int kind) {
        return createIndex(locateTable(table, true), table, key, kind);
    }

    public boolean createIndex(Class table, String key, boolean unique) {
        return createIndex(locateTable(table, true), table, key, unique ? INDEX_KIND_UNIQUE : INDEX_KIND_DEFAULT);
    }

    public boolean createIndex(Class table, String key, boolean unique, boolean caseInsensitive, boolean thick) {
        int kind = INDEX_KIND_DEFAULT;
        if (unique)
            kind |= INDEX_KIND_UNIQUE;
        if (caseInsensitive)
            kind |= INDEX_KIND_CASE_INSENSITIVE;
        if (thick)
            kind |= INDEX_KIND_THICK;
        return createIndex(locateTable(table, true), table, key, kind);
    }

    public boolean createIndex(Class table, String key, boolean unique, boolean caseInsensitive, boolean thick,
            boolean randomAccess) {
        int kind = INDEX_KIND_DEFAULT;
        if (unique)
            kind |= INDEX_KIND_UNIQUE;
        if (caseInsensitive)
            kind |= INDEX_KIND_CASE_INSENSITIVE;
        if (thick)
            kind |= INDEX_KIND_THICK;
        if (randomAccess)
            kind |= INDEX_KIND_RANDOM_ACCESS;
        return createIndex(locateTable(table, true), table, key, kind);
    }

    private boolean createIndex(Table t, Class c, String key, int kind) {
        if (t.indicesMap.get(key) == null) {
            boolean unique = (kind & INDEX_KIND_UNIQUE) != 0;
            boolean caseInsensitive = (kind & INDEX_KIND_CASE_INSENSITIVE) != 0;
            FieldIndex index = (kind & INDEX_KIND_REGEX) != 0
                    ? storage.createRegexIndex(c, key, caseInsensitive, 3)
                    : (kind & INDEX_KIND_RANDOM_ACCESS) != 0
                            ? storage.createRandomAccessFieldIndex(c, key, unique, caseInsensitive)
                            : storage.createFieldIndex(c, key, unique, caseInsensitive, (kind & INDEX_KIND_THICK) != 0);
            t.indicesMap.put(key, index);
            t.indices.add(index);
            for (Object obj : t.extent) {
                if (!index.put(obj) && unique) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean dropIndex(Class table, String key) {
        Table t = locateTable(table, true);
        FieldIndex index = (FieldIndex) t.indicesMap.remove(key);
        if (index != null) {
            t.indices.remove(t.indices.indexOf(index));
            return true;
        }
        return false;
    }

    public GenericIndex getIndex(Class table, String key) {
        for (Class c = table; c != null; c = c.getSuperclass()) {
            Table t = locateTable(c, false, false);
            if (t != null) {
                synchronized (t.indicesMap) {
                    GenericIndex index = (GenericIndex) t.indicesMap.get(key);
                    if (index != null) {
                        return index;
                    }
                    if (autoIndices && key.indexOf('.') < 0) {
                        try {
                            c.getDeclaredField(key);
                        } catch (NoSuchFieldException x) {
                            continue;
                        }
                        StorageListener listener = storage.getListener();
                        if (listener != null) {
                            listener.indexCreated(c, key);
                        }
                        createIndex(t, c, key, INDEX_KIND_DEFAULT);
                        return (GenericIndex) t.indicesMap.get(key);
                    }
                }
            }
        }
        return null;
    }

    public HashMap getIndices(Class table) {
        Table t = locateTable(table, true, false);
        return t == null ? new HashMap() : t.indicesMap;
    }

    public void excludeFromAllIndices(Object record) {
        excludeFromAllIndices(record.getClass(), record);
    }

    public void excludeFromAllIndices(Class table, Object record) {
        if (multithreaded) {
            checkTransaction();
            metadata.sharedLock();
        }
        boolean fullTextIndexed = false;
        for (Class c = table; c != null; c = c.getSuperclass()) {
            Table t = tables.get(c);
            if (t != null) {
                if (multithreaded) {
                    t.extent.exclusiveLock();
                }
                Iterator iterator = t.indicesMap.values().iterator();
                while (iterator.hasNext()) {
                    FieldIndex index = (FieldIndex) iterator.next();
                    index.remove(record);
                }
                if (t.fullTextIndexableFields.size() != 0) {
                    fullTextIndexed = true;
                }
            }
        }
        if (record instanceof FullTextSearchable || fullTextIndexed) {
            if (multithreaded) {
                metadata.fullTextIndex.exclusiveLock();
            }
            metadata.fullTextIndex.delete(record);
        }
    }

    public boolean excludeFromIndex(Object record, String key) {
        return excludeFromIndex(record.getClass(), record, key);
    }

    public boolean excludeFromIndex(Class table, Object record, String key) {
        Table t = locateTable(table, true);
        FieldIndex index = (FieldIndex) t.indicesMap.get(key);
        if (index != null) {
            index.remove(record);
            return true;
        }
        return false;
    }

    public boolean includeInAllIndices(Object record) {
        return includeInAllIndices(record.getClass(), record);
    }

    public boolean includeInAllIndices(Class table, Object record) {
        if (multithreaded) {
            checkTransaction();
        }
        ArrayList wasInsertedIn = new ArrayList();
        StringBuffer fullText = new StringBuffer();
        for (Class c = table; c != null; c = c.getSuperclass()) {
            Table t = tables.get(c);
            if (t != null) {
                if (multithreaded) {
                    t.extent.exclusiveLock();
                }
                Iterator iterator = t.indicesMap.values().iterator();
                while (iterator.hasNext()) {
                    FieldIndex index = (FieldIndex) iterator.next();
                    if (index.put(record)) {
                        wasInsertedIn.add(index);
                    } else if (index.isUnique()) {
                        iterator = wasInsertedIn.iterator();
                        while (iterator.hasNext()) {
                            Object idx = iterator.next();
                            if (idx instanceof IPersistentSet) {
                                ((IPersistentSet) idx).remove(record);
                            } else {
                                ((FieldIndex) idx).remove(record);
                            }
                        }
                        return false;
                    }
                }
                for (Field f : t.fullTextIndexableFields) {
                    Object text;
                    try {
                        text = f.get(record);
                    } catch (IllegalAccessException x) {
                        throw new IllegalAccessError();
                    }
                    if (text != null) {
                        fullText.append(' ');
                        fullText.append(text.toString());
                    }
                }
            }
        }
        if (record instanceof FullTextSearchable || fullText.length() != 0) {
            if (multithreaded) {
                metadata.fullTextIndex.exclusiveLock();
            }
            if (record instanceof FullTextSearchable) {
                metadata.fullTextIndex.add((FullTextSearchable) record);
            } else {
                metadata.fullTextIndex.add(record, new StringReader(fullText.toString()), null);
            }
        }
        return true;
    }

    public boolean includeInIndex(Object record, String key) {
        return includeInIndex(record.getClass(), record, key);
    }

    public boolean includeInIndex(Class table, Object record, String key) {
        Table t = locateTable(table, true);
        FieldIndex index = (FieldIndex) t.indicesMap.get(key);
        if (index != null) {
            return index.put(record) || !index.isUnique();
        }
        return false;
    }

    public void updateKey(Object record, String key, Object value) {
        updateKey(record.getClass(), record, key, value);
    }

    public void updateKey(Class table, Object record, String key, Object value) {
        excludeFromIndex(table, record, key);
        Field f = ClassDescriptor.locateField(table, key);
        if (f == null) {
            throw new StorageError(StorageError.INDEXED_FIELD_NOT_FOUND, table.getName());
        }
        try {
            f.set(record, value);
        } catch (Exception x) {
            throw new StorageError(StorageError.ACCESS_VIOLATION, x);
        }
        storage.modify(record);
        includeInIndex(table, record, key);
    }

    public <T> IterableIterator<T> select(Class table, String predicate) {
        return select(table, predicate, false);
    }

    public <T> IterableIterator<T> select(Class table, String predicate, boolean forUpdate) {
        Query q = prepare(table, predicate, forUpdate);
        return q.execute(getRecords(table));
    }

    public <T> Query<T> prepare(Class table, String predicate) {
        return prepare(table, predicate, false);
    }

    public <T> Query<T> prepare(Class table, String predicate, boolean forUpdate) {
        Query<T> q = createQuery(table, forUpdate);
        q.prepare(table, predicate);
        return q;
    }

    public <T> Query<T> createQuery(Class table) {
        return createQuery(table, false);
    }

    public <T> Query<T> createQuery(Class table, boolean forUpdate) {
        Table t = locateTable(table, forUpdate, false);
        Query q = storage.createQuery();
        q.setIndexProvider(this);
        q.setClass(table);
        while (t != null) {
            q.setClassExtent(t.extent,
                    multithreaded ? forUpdate ? Query.ClassExtentLockType.Exclusive : Query.ClassExtentLockType.Shared
                            : Query.ClassExtentLockType.None);
            Iterator iterator = t.indicesMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                FieldIndex index = (FieldIndex) entry.getValue();
                String key = (String) entry.getKey();
                q.addIndex(key, index);
            }
            t = locateTable(t.type.getSuperclass(), forUpdate, false);
        }
        return q;
    }

    public <T> IterableIterator<T> getRecords(Class table) {
        return getRecords(table, false);
    }

    public <T> IterableIterator<T> getRecords(Class table, boolean forUpdate) {
        Table t = locateTable(table, forUpdate, false);
        return new IteratorWrapper<T>(
                t == null ? new LinkedList<T>().iterator() : new ClassFilterIterator(table, t.extent.iterator()));
    }

    public int countRecords(Class table) {
        return countRecords(table, false);
    }

    public int countRecords(Class table, boolean forUpdate) {
        Table t = locateTable(table, forUpdate, false);
        return t == null ? 0 : t.extent.size();
    }

    public Storage getStorage() {
        return storage;
    }

    public FullTextIndex getFullTextIndex() {
        return metadata.fullTextIndex;
    }

    public FullTextSearchResult searchPrefix(String prefix, int maxResults, int timeLimit, boolean sort) {
        if (multithreaded) {
            checkTransaction();
            metadata.fullTextIndex.sharedLock();
        }
        return metadata.fullTextIndex.searchPrefix(prefix, maxResults, timeLimit, sort);
    }

    public FullTextSearchResult search(String query, String language, int maxResults, int timeLimit) {
        if (multithreaded) {
            checkTransaction();
            metadata.fullTextIndex.sharedLock();
        }
        return metadata.fullTextIndex.search(query, language, maxResults, timeLimit);
    }

    public Iterator<FullTextIndex.Keyword> getKeywords(String prefix) {
        if (multithreaded) {
            checkTransaction();
            metadata.fullTextIndex.sharedLock();
        }
        return metadata.fullTextIndex.getKeywords(prefix);
    }

    public FullTextSearchResult search(FullTextQuery query, int maxResults, int timeLimit) {
        if (multithreaded) {
            checkTransaction();
            metadata.fullTextIndex.sharedLock();
        }
        return metadata.fullTextIndex.search(query, maxResults, timeLimit);
    }

    static class Metadata extends PersistentResource {
        Index metaclasses;
        FullTextIndex fullTextIndex;

        Metadata(Storage storage, Index index, FullTextSearchHelper helper) {
            super(storage);
            metaclasses = index;
            fullTextIndex = (helper != null)
                    ? storage.createFullTextIndex(helper)
                    : storage.createFullTextIndex();
        }

        Metadata(Storage storage, FullTextSearchHelper helper) {
            super(storage);
            metaclasses = storage.createIndex(String.class, true);
            fullTextIndex = (helper != null)
                    ? storage.createFullTextIndex(helper)
                    : storage.createFullTextIndex();
        }

        Metadata() {
        }
    }

    static class Table extends Persistent {
        IPersistentSet extent;
        Link indices;

        transient HashMap indicesMap = new HashMap();
        transient ArrayList<Field> fullTextIndexableFields;
        transient Class type;
        transient FieldIndex autoincrementIndex;

        void setClass(Class cls) {
            type = cls;
            fullTextIndexableFields = new ArrayList<Field>();
            for (Field f : cls.getDeclaredFields()) {
                FullTextIndexable idx = (FullTextIndexable) f.getAnnotation(FullTextIndexable.class);
                if (idx != null) {
                    try {
                        f.setAccessible(true);
                    } catch (Exception x) {
                    }
                    fullTextIndexableFields.add(f);
                }
            }
        }

        public void onLoad() {
            for (int i = indices.size(); --i >= 0;) {
                FieldIndex index = (FieldIndex) indices.get(i);
                Field key = index.getKeyFields()[0];
                indicesMap.put(key.getName(), index);
                Indexable idx = (Indexable) key.getAnnotation(Indexable.class);
                if (idx != null && idx.autoincrement()) {
                    autoincrementIndex = index;
                }
            }
        }

        public void deallocate() {
            extent.deallocate();
            for (Object index : indicesMap.values()) {
                ((FieldIndex) index).deallocate();
            }
            super.deallocate();
        }
    }

    HashMap<Class, Table> tables;
    Storage storage;
    Metadata metadata;
    boolean multithreaded;
    boolean autoRegisterTables;
    boolean autoIndices;
    boolean globalClassExtent;
    boolean searchBaseClasses;
}

class ClassFilterIterator implements Iterator {
    public boolean hasNext() {
        return obj != null;
    }

    public Object next() {
        Object curr = obj;
        if (curr == null) {
            throw new NoSuchElementException();
        }
        moveNext();
        return curr;
    }

    public ClassFilterIterator(Class c, Iterator i) {
        cls = c;
        iterator = i;
        moveNext();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void moveNext() {
        obj = null;
        while (iterator.hasNext()) {
            Object curr = iterator.next();
            if (cls.isInstance(curr)) {
                obj = curr;
                return;
            }
        }
    }

    private Iterator iterator;
    private Class cls;
    private Object obj;
}
