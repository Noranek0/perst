package org.garret.perst.fulltext;

import org.garret.perst.*;
import java.io.Reader;
import java.util.Iterator;

public interface FullTextIndex extends IPersistent, IResource
{
    void add(FullTextSearchable obj);
    void add(Object obj, Reader text, String language);
    void delete(Object obj);
    void clear();

    public interface Keyword {
        String getNormalForm();
        long getNumberOfOccurrences();
    }

    Iterator<Keyword> getKeywords(String prefix);
    FullTextSearchResult searchPrefix(String prefix, int maxResults, int timeLimit, boolean sort);
    FullTextSearchResult search(String query, String language, int maxResults, int timeLimit);
    FullTextSearchResult search(FullTextQuery query, int maxResults, int timeLimit);

    int getNumberOfWords();
    int getNumberOfDocuments();

    FullTextSearchHelper getHelper();
}