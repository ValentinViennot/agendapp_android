package fr.agendapp.app.utils.filters;

import android.app.Activity;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
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
    private static List<SelectableFilter> subjects;
    private static List<SelectableFilter> flags;
    // tableau 2D de filtres
    // Filter[] chaque case de ce tableau doit être vérifiée
    // Filter[i][] chaque case du sous tableau correspond à un Filter (filtre)
    // Il suffit qu'une seule condition j de Filter[i][j] soit validée pour que la condition i soit validée
    private static Tab[] filters = Tab.init(Filter.NB_TYPES);
    // Permet de regrouper les filtres de même types dans une même condition ET
    private int type;
    private boolean active;

    Filter(int type) {
        this.type = type;
        this.active = false;
    }

    public static List<SelectableFilter> getSubjects() {
        return subjects;
    }

    public static List<SelectableFilter> getFlags() {
        return flags;
    }

    /**
     * Défini les filtres selectionnables
     *
     * @param activity Activity appelante
     */
    public static void setSelectables(Activity activity) {
        HashMap<String, SelectableFilter> filtersubjects = new HashMap<>();
        HashMap<Integer, SelectableFilter> filterflags = new HashMap<>();
        List<List<Work>> list = new LinkedList<>();
        list.add(Work.getComingwork(activity));
        list.add(Work.getPastwork(activity));
        for (List<Work> l : list)
            for (Work w : l) {
                if (filtersubjects.containsKey(w.getSubject()))
                    filtersubjects.get(w.getSubject()).inc();
                else
                    filtersubjects.put(w.getSubject(), new SelectableFilter(new FilterSubject(w.getSubject())));
                if (filterflags.containsKey(w.getFlag()))
                    filterflags.get(w.getFlag()).inc();
                else
                    filterflags.put(w.getFlag(), new SelectableFilter(new FilterFlag(w.getFlag())));
            }
        subjects = new LinkedList<>(filtersubjects.values());
        flags = new LinkedList<>(filterflags.values());
        Collections.sort(subjects);
        Collections.sort(flags);
    }

    /**
     * Ajout d'un filtre
     * Exemple d'utilisation :
     * addFilter(new FilterFlag(2))
     * pour filter sur les devoirs possédant un drapeau de couleur 2
     *
     * @param filter Filtre a appliquer
     */
    public static void addFilter(Filter filter) {
        filters[filter.getType()].add(filter);
        filter.active = true;
    }

    static void clearFilter(Filter filter) {
        filters[filter.getType()].remove(filter);
        filter.active = false;
    }

    /* Filtrage des devoirs */

    /**
     * Effacer tous les filtres actifs
     */
    public static void clearFilter() throws ConcurrentModificationException {
        for (Tab list : filters)
            for (Filter filter : list)
                clearFilter(filter);
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
        for (Tab block : filters) {
            // Si le groupe contient des filtres
            if (block.size() > 0) {
                // Le devoir doit correspondre à au moins un filtre du groupe
                // On suppose que c'est faux
                boolean b = false;
                // Tant que c'est faux, on continue d'essayer de le montrer
                // Jusqu'à avoir testé tous les filtres du groupe
                for (Filter filter : block) {
                    // Si le devoir correspond au filtre (condition suffisante)
                    if (filter.correspond(w)) {
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
     * @return Filtres actuellement actifs
     */
    public static List<Filter> getActiveFilters() {
        List<Filter> list = new LinkedList<>();
        for (Tab filter : filters)
            list.addAll(filter);
        return list;
    }

    boolean isActive() {
        return active;
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
