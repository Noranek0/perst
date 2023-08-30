package org.garret.perst.fulltext;

import org.garret.perst.IPersistent;
import java.io.Reader;

public interface FullTextSearchable extends IPersistent
{
    Reader getText();
    String getLanguage();
}