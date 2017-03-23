package fr.agendapp.app.pages;

import fr.agendapp.app.objects.Work;

/**
* Affichage des archives plutot que des devoirs à venir
*/
public class ArchivesPage extends WorkPage {

    protected final int SYNC_DELAY = 5000;

    @Override
    protected void setHomeworks() {
        this.homeworks = Work.getPastwork(this.getContext());
    }

    @Override
    public boolean isArchives() {
        return true;
    }

    @Override
    protected void planNextSync() {
        planNextSync(SYNC_DELAY);
    }

}
