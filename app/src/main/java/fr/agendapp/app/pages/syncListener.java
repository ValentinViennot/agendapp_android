package fr.agendapp.app.pages;

public interface SyncListener {
    void onSync();

    void onSyncNotAvailable();

    boolean isArchives();
}
