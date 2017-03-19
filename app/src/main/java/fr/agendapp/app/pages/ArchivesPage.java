package fr.agendapp.app.pages;

import fr.agendapp.app.objects.Work;

/**
* Affichage des archives plutot que des devoirs à venir
*/
public class ArchivesPage extends WorkPage {

    @Override
    protected void setHomeworks() {
        this.homeworks = Work.getPastwork(this.getContext());
    }

    @Override
    public boolean isArchives() {
        return true;
    }

}
