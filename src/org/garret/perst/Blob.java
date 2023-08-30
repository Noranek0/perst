package org.garret.perst;

import java.io.*;

public interface Blob extends IPersistent, IResource {
    RandomAccessInputStream getInputStream();
    RandomAccessOutputStream getOutputStream();
    RandomAccessOutputStream getOutputStream(boolean multisession);
    RandomAccessOutputStream getOutputStream(long position, boolean multisession);

    int ENABLE_SEGMENT_CACHING = 1;
    int DOUBLE_SEGMENT_SIZE = 2;
    int TRUNCATE_LAST_SEGMENT = 4;
    int APPEND = 8;

    RandomAccessInputStream getInputStream(int flags);
    RandomAccessOutputStream getOutputStream(int flags);
};