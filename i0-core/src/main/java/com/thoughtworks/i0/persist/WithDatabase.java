package com.thoughtworks.i0.persist;

import com.thoughtworks.i0.persist.config.DatabaseConfiguration;

public interface WithDatabase {
    DatabaseConfiguration getDatabase();
}
