package fr.agendapp.app.pages;

import java.util.List;

import fr.agendapp.app.objects.Work;

/**
* Affichage des archives plutot que des devoirs à venir
*/
public class ArchivesPage extends WorkPage {

    // Réécrire type et getHomeworks()


    private List<Work> getHomeworks() {
        return Work.getPastwork(this.getContext());
    }

}
