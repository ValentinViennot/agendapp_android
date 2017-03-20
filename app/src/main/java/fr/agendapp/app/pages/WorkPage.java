package fr.agendapp.app.pages;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.ParseFactory;
import fr.agendapp.app.objects.Filter;
import fr.agendapp.app.objects.Invite;
import fr.agendapp.app.objects.Section;
import fr.agendapp.app.objects.Work;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static android.content.Context.MODE_PRIVATE;

/**
 * Page (Vue) d'affichage des devoirs à faire
 * Une Vue est un composant affichable dans une activité
 */
public class WorkPage extends Fragment {

    static String type = "devoirs";

    /**
     * Filtre ajouté par l'utilisateur
     */
    Filter filter;
    /**
     * Filtre ajouté par l'ordinateur
     */
    String autofilter;

    Invite[] invits;

    static List<Work> homeworks;
    // TODO penser l'affichage d'une liste de section contenant elles même une liste de devoirs

    Section[] sections;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // TODo debug
        SharedPreferences preferences = getActivity().getSharedPreferences(App.TAG, MODE_PRIVATE);
        homeworks = new LinkedList<>();
        homeworks.addAll(ParseFactory.parseWork(
                preferences.getString("devoirs", "[]")
        ));
        //
        return recyclerView;
    }

    /**
     * Un Adapter est une classe servant à afficher des listes d'objets
     * Ici une liste de "Work" dont l'affichage est défini dans la classe interne Work.ViewHolder
     * Un RecyclerView est simplement une vue réutilisée pour l'affichage de chaque élément
     * Finalement,
     * On initialise cet Adapter pour y afficher des "Work" (devoirs) via une "Vue Recyclée"
     */
    public static class ContentAdapter extends RecyclerView.Adapter<Work.ViewHolder> {

        ContentAdapter() {
        }

        @Override
        public Work.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Work.ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(Work.ViewHolder holder, int position) {
            holder.setWork(WorkPage.homeworks.get(position % getItemCount()));
        }

        @Override
        public int getItemCount() {
            return WorkPage.homeworks.size();
        }
    }

    /**
     * Méthode qui renvoie une liste de devoirs après avoir appliqué un filtre
     *
     * @return : la liste de devoirs filtrés
     */
    private List<Work> applyFilter() {
        List<Work> res = new LinkedList<>();
        for (fr.agendapp.app.objects.Work w : homeworks) {
            boolean intermediaire = false;
            for (String s : filter.getMatieres()) {
                if (w.getMatiere().contains(s) || filter.getMatieres() == null) {
                    intermediaire = true;
                    break;
                }
            }
            if (intermediaire) {
                for (Integer i : filter.getFlag()) {
                    if (w.getFlag() == i || filter.getFlag() == null) {
                        intermediaire = true;
                        break;
                    } else {
                        intermediaire = false;
                    }
                }

            }
            if (intermediaire) {
                if ((filter.isFait() == w.isFait()) && (filter.getAuteur().indexOf(w.getAuteur()) != -1 || filter.getAuteur() == null) &&
                        (filter.getResearch().indexOf(w.getText()) != -1 || filter.getResearch() == null)) {
                    res.add(w);
                }

            }

        }
        return res;
    }

    private void insert(Work w) {
        ListIterator<Work> i = homeworks.listIterator();
        while (i.hasNext()) {
            Work h = i.next();
            if (h.getDate().compareTo(w.getDate()) <= 0) {
                i.add(w);
                return;
            }
        }
    }

    // Je veux rien supprimer de ce qui est au dessus donc on verra
    // Nouvelle solution pour l'application des filtres :

    final int MAX_FILTERS;
    // tableau 2D de filtres
// Filter[] chaque case de ce tableau doit être vérifiée
// Filter[i][] chaque case du sous tableau correspond à un Filter (filtre)
// Il suffit qu'une seule condition j de Filter[i][j] soit validée pour que la condition i soit validée
    Filter[][] filters = new Filter[Filter.NB_TYPES][MAX_FILTERS];

    List<Work> homeworks;

    List<Work> applyFilters2() {
        List<Work> r;
        // Pour chaque devoir
        for (Work w : homeworks) {
            if (validateFilters(w)) r.add(w);
        }
        return r;
    }

    /**
     * @return true si le devoir valide les conditions Filter[][]
     */
    boolean validateFilters(Work w) {
        // Pour chaque groupe de filtres
        for (int i=0; i<filters.length; ++i) {
            if (filters[i]!=null) {
                // Le devoir doit correspondre à au moins un filtre du groupe
                // On suppose que c'est faux
                boolean b = false;
                int j = 0;
                // Tant que c'est faux, on continue d'essayer de le montrer
                // Jusqu'à avoir testé tous les filtres du groupe
                while(!b&&j<filters[i].length) {
                    // Si le devoir correspond au filtre
                    // b vaudra true
                    // la boucle s'arrêtera
                    // et on testera le groupe suivant
                    if(filters[i][j]!=null)
                        b = filters[i][j].correspond(w);
                    // Filtre suivant
                    j++;
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
     * Ajout d'un filtre
     * Exemple d'utilisation :
     * addFilter(new FilterFlag(2))
     * pour filter sur les devoirs possédant un drapeau de couleur 2
     */
    void addFilter(Filter filter) {
        // Pour le type de filtre demandé
        boolean intermediaire == true;
        for (int i=0; i<filters[filter.getType()].length; ++i) {
            // On cherche la première case non nulle
            if (filters[filter.getType()][i]!=null) {
                // On y ajoute le filtre
                filters[filter.getType()][i] = filter;
                intermediaire = false;
                // Quitte la méthode
                return;
            }
        }
        if (intermediaire){
            System.out.println("Erreur : Trop de filtres, limite a +" MAX_FILTERS)
        }
        // Si la méthode n'a pas été quittée, il y a eu une erreur
        // TODO ajouter une notification d'erreur (ex "Trop de filtres : Limité à MAX_FILTERS !")
        //FAIT
    }

    void clearFilter(Filter filter) {
        filter=null;
    }

}

