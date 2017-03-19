package fr.agendapp.app.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderDecoration;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.DateFactory;
import fr.agendapp.app.objects.Header;
import fr.agendapp.app.objects.Work;

/**
 * Page (Vue) d'affichage des devoirs à faire
 * Une Vue est un composant affichable dans une activité
 */
public class WorkPage extends Fragment {

    private String type = "devoirs";
    private List<Header> headers;
    private List<Header> subheaders;
    private List<Work> homeworks;

    private static long getLongId(int position, List<Header> headers) {
        int i = headers.size(), total;
        ListIterator<Header> li = headers.listIterator(i);
        // Iteration dans le sens inversé
        while (li.hasPrevious()) {
            i--;
            total = li.previous().getTo();
            if (position >= total)
                return i + position / total;
        }
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        final DoubleHeaderAdapter adapter = new DoubleHeaderAdapter();
        DoubleHeaderDecoration decor = new DoubleHeaderDecoration(adapter);

        headers = new LinkedList<>();
        subheaders = new LinkedList<>();
        homeworks = new LinkedList<>();
        recalcSections();//TODO Notifier l'adapter à la fin du recalc (ASync)

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }

    /**
     * Calcule les en-têtes et leur emplacement en fonction de la liste de devoirs
     */
    private void recalcSections() {
        // Liste de devoirs
        homeworks = Work.getComingwork(this.getContext());
        // Cas où la liste de devoirs est vide
        if (homeworks.size() == 0) {
            headers = new LinkedList<>();
            subheaders = new LinkedList<>();
            return;
        }
        // Récupére une instance de Calendrier
        // Pour la date de la section en cours
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // Et une instance pour comparer
        Calendar cal2 = Calendar.getInstance();
        // Sections
        Header month = null;
        Header day = null;
        // parcours avec iterateur
        int i = 0;
        for (Work w : homeworks) {
            // Date du devoir traité
            cal2.setTime(w.getDate());
            // Si le mois et/ou le jour sont différents
            if (cal.get(Calendar.MONTH) != cal2.get(Calendar.MONTH) || cal.get(Calendar.DAY_OF_MONTH) != cal2.get(Calendar.DAY_OF_MONTH)) {
                // Mois différent => En tête de mois
                if (cal.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)) {
                    // Termine la section précédente
                    if (month != null) {
                        month.setTo(i);
                    }
                    // Ouvre une nouvelle section
                    month = new Header(i, DateFactory.getMonthName(this.getContext(), cal2.get(Calendar.MONTH)));
                    headers.add(month);
                }
                // Date différente => En tete de jour
                // Termine la section précédente
                if (day != null) {
                    day.setTo(i);
                }
                day = new Header(i, DateFactory.getWeekName(this.getContext(), cal2.get(Calendar.DAY_OF_WEEK)) + " " + cal2.get(Calendar.DAY_OF_MONTH));
                subheaders.add(day);
                // Met à jour la date de la section en cours
                cal.setTime(w.getDate());
            }
            i++;
        }
        if (month != null) month.setTo(i);
        if (day != null) day.setTo(i);
    }

    /* TODO ?
    private void insert(Work w) {
        ListIterator<Work> i = homeworks.listIterator();
        while (i.hasNext()) {
            Work h = i.next();
            if (h.getDate().compareTo(w.getDate()) <= 0) {
                i.add(w);
                return;
            }
        }
    }*/

    class DoubleHeaderAdapter extends RecyclerView.Adapter<Work.ViewHolder> implements
            ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderAdapter<DoubleHeaderAdapter.HeaderHolder, DoubleHeaderAdapter.SubHeaderHolder> {

        DoubleHeaderAdapter() {
        }

        @Override
        public Work.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Work.ViewHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
        }

        @Override
        public void onBindViewHolder(Work.ViewHolder holder, int position) {
            holder.setWork(homeworks.get(position));
        }

        @Override
        public int getItemCount() {
            return homeworks.size();
        }

        @Override
        public long getHeaderId(int position) {
            return getLongId(position, headers);
        }

        @Override
        public long getSubHeaderId(int position) {
            return getLongId(position, subheaders);
        }

        @Override
        public HeaderHolder onCreateHeaderHolder(ViewGroup parent) {
            return new HeaderHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public SubHeaderHolder onCreateSubHeaderHolder(ViewGroup parent) {
            return new SubHeaderHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindHeaderHolder(HeaderHolder viewholder, int position) {
            viewholder.title.setText(headers.get((int) getHeaderId(position)).getTitle());
        }

        @Override
        public void onBindSubHeaderHolder(SubHeaderHolder viewholder, int position) {
            viewholder.title.setText(subheaders.get((int) getSubHeaderId(position)).getTitle());
        }

        class HeaderHolder extends RecyclerView.ViewHolder {
            TextView title;

            HeaderHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.header, parent, false));
                title = (TextView) itemView.findViewById(R.id.headertitle);
            }
        }

        class SubHeaderHolder extends RecyclerView.ViewHolder {
            TextView title;

            SubHeaderHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.subheader, parent, false));
                title = (TextView) itemView.findViewById(R.id.subheadertitle);
            }
        }
    }
}

