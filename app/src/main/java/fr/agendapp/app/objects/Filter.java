package fr.agendapp.app.objects;

import java.util.List;

/**
 * Created by Charline on 14/03/2017.
 */
abstract class Filter {

    static final int NB_TYPES = 6;

    // Chaine de caractère contenue dans le texte du devoir
    static final int USER_TYPE = 0;
    // Drapeau correspond
    static final int FLAG_TYPE = 1;
    // Matière correspond
    static final int SUBJECT_TYPE = 2;
    // Fait/pas Fait correspond
    static final int DONE_TYPE =3;
    //Autheur correspond
    static final int AUTHOR_TYPE = 4;
    //Date correspond
    static final int DATE_TYPE = 5;

    // Permet de regrouper les filtres de même types dans une même condition ET
    int type;

    Filter(int type) {
        this.type = type;
    }

    /**
     * @return true si le devoir correspond au filtre
     */
    abstract boolean correspond(Work w);

    int getType() {
        return this.type;
    }
}
