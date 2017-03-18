package fr.agendapp.app.pages;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.ParseFactory;
import fr.agendapp.app.objects.Section;
import fr.agendapp.app.objects.Work;

import static android.content.Context.MODE_PRIVATE;

/**
 * Page (Vue) d'affichage des devoirs à faire
 * Une Vue est un composant affichable dans une activité
 */
public class WorkPage extends Fragment {

    // TODO supprimer
    static List<Work> homeworks;
    String type = "devoirs";
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

}

