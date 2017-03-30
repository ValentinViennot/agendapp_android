package fr.agendapp.app.utils;

import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.objects.Work;

/**
 * @author Valentin Viennot
 * @author Charline Bardin
 */
public abstract class Filter {

    // Filtrage par...
    // Chaine de caractère contenue dans le texte du devoir
    static final int USER_TYPE = 0;
    // Drapeau correspond
    static final int FLAG_TYPE = 1;
    // Matière correspond
    static final int SUBJECT_TYPE = 2;
    // Fait/pas Fait correspond
    static final int DONE_TYPE = 3;
    //Autheur correspond
    static final int AUTHOR_TYPE = 4;
    //Date correspond
    static final int DATE_TYPE = 5;
    // Nombre de type de filtres différents
    private static final int NB_TYPES = 6;
    // tableau 2D de filtres
    // Filter[] chaque case de ce tableau doit être vérifiée
    // Filter[i][] chaque case du sous tableau correspond à un Filter (filtre)
    // Il suffit qu'une seule condition j de Filter[i][j] soit validée pour que la condition i soit validée
    private static Tab[] filters = Tab.init(Filter.NB_TYPES);
    // Permet de regrouper les filtres de même types dans une même condition ET
    private int type;

    Filter(int type) {
        this.type = type;
    }

    /**
     * Ajout d'un filtre
     * Exemple d'utilisation :
     * addFilter(new FilterFlag(2))
     * pour filter sur les devoirs possédant un drapeau de couleur 2
     *
     * @param filter              Filtre a appliquer
     */
    public static void addFilter(Filter filter) {
        filters[filter.getType()].add(filter);
    }

    /* Filtrage des devoirs */

    public static void clearFilter(Filter filter) {
        filters[filter.getType()].remove(filter);
    }

    public static List<Work> applyFilters(List<Work> homeworks) {
        List<Work> r = new LinkedList<>();
        // Pour chaque devoir
        for (Work w : homeworks) {
            if (validateFilters(w)) r.add(w);
        }
        return r;
    }

    /**
     * @param w Devoir à tester
     * @return true si le devoir valide les conditions Filter[][]
     */
    private static boolean validateFilters(Work w) {
        // Pour chaque groupe de filtres
        for (int i = 0; i < filters.length; ++i) {
            // Si le groupe contient des filtres
            if (filters[i].size() > 0) {
                // Le devoir doit correspondre à au moins un filtre du groupe
                // On suppose que c'est faux
                boolean b = false;
                // Tant que c'est faux, on continue d'essayer de le montrer
                // Jusqu'à avoir testé tous les filtres du groupe
                for (Filter f : filters[i]) {
                    // Si le devoir correspond au filtre (condition suffisante)
                    if (f.correspond(w)) {
                        // On valide le groupe de filtres
                        b = true;
                        // Et on quitte la boucle
                        break;
                    }
                }
                // Si ce groupe de filtre n'est pas validé, rien ne sert de tester les suivants
                // le devoir ne correspond pas
                if (!b) return false;
            }
        }
        // Si la méthode n'a jamais renvoyé false, alors le devoir correspond
        return true;
    }

    /**
     * @return true si le devoir correspond au filtre
     */
    public abstract boolean correspond(Work w);

    /**
     * @return Type de filtre (cf constantes)
     */
    private int getType() {
        return this.type;
    }

    // Pour contourner la restriction interdisant la création d'un tableau de listes génériques
    private static class Tab extends LinkedList<Filter> {
        static Tab[] init(int size) {
            Tab[] tab = new Tab[size];
            for (int i = 0; i < size; ++i)
                tab[i] = new Tab();
            return tab;
        }
    }

}
