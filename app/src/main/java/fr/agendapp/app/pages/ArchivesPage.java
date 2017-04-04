package fr.agendapp.app.pages;

/**
 * Affichage des archives plutot que des devoirs à venir
 */
public class ArchivesPage extends WorkPage {

    @Override
    public boolean isArchives() {
        return true;
    }

    @Override
    protected void planNextSync() {
        int SYNC_DELAY = 5000;
        planNextSync(SYNC_DELAY);
    }

}
