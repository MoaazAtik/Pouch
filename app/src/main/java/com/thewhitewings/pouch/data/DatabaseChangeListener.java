package com.thewhitewings.pouch.data;

/**
 * Interface for listening to changes in the database.
 */
public interface DatabaseChangeListener {

    /**
     * Called when the database has changed.
     */
    void onDatabaseChanged();
}
