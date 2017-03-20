package fr.agendapp.app.objects;

import java.util.List;

/**
 * Created by Charline on 14/03/2017.
 */
public class Filter {

    private List<String> matieres;
    private List<Integer> flag;
    private boolean fait;
    private String research;
    private String auteur;

    public Filter(){
        this.matieres=null;
        this.flag=null;
        this.fait=false;
        this.research=null;
    }

    public List<String> getMatieres() {
        return matieres;
    }

    public List<Integer> getFlag() {
        return flag;
    }

    public boolean isFait() {
        return fait;
    }

    public String getResearch() {
        return research;
    }

    public String getAuteur() {
        return auteur;
    }

    //encore une fois je sais pas trop ce qu'il va falloir rajouter, donc je laisse ce qu'il y a au dessus
    // Nouvelle version de Section

    abstract class Filter {

        static final int NB_TYPES = 6;

        // Chaine de caractère contenue dans le texte du devoir
        static final int USER_FILTER = 0;
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
}
}
