package fr.agendapp.app.pages;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderDecoration;
import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.DateFactory;
import fr.agendapp.app.factories.Pending;
import fr.agendapp.app.objects.Header;
import fr.agendapp.app.objects.Work;

/**
 * Page (Vue) d'affichage des devoirs à faire
 * Une Vue est un composant affichable dans une activité
 */
public class WorkPage extends Fragment implements SyncListener {

    protected List<Header> headers;
    protected List<Header> subheaders;
    protected List<Work> homeworks;

    protected DoubleHeaderAdapter adapter;
    // TODO Timer devrait être déprécié... Remplacer par ScheduledExecutorService (pas urgent)
    protected Timer timer = new Timer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        headers = new LinkedList<>();
        subheaders = new LinkedList<>();
        homeworks = new LinkedList<>();

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        adapter = new DoubleHeaderAdapter(this);
        DoubleHeaderDecoration decor = new DoubleHeaderDecoration(adapter);

        // Simule l'arrivée de nouvelles données pour forcer le premier affichage
        onSync();
        // La planification est inclue dans le callback onSync

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }

    /**
     * Calcule les en-têtes et leur emplacement en fonction de la liste de devoirs
     */
    protected void recalcSections() {
        Log.i(App.TAG, "test : recalcSection ");
        // Réinitialisation de la liste de devoirs
        setHomeworks();
        // Réinitialisation des listes d'headers
        headers = new LinkedList<>();
        subheaders = new LinkedList<>();
        // Cas où la liste de devoirs est vide
        if (homeworks.size() == 0) return;
        // Récupére une instance de Calendrier
        // Pour la date de la section en cours
        Calendar cal = Calendar.getInstance();
        // On se place à la date de "The epoch" (car on veut forcément un premier header/subheader)
        cal.setTime(new Date(0L));
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
        Log.i(App.TAG, "test : headers size : " + headers.size());
        for (Header h : headers) Log.i(App.TAG, h.getTitle());
        Log.i(App.TAG, "test : subheaders size : " + subheaders.size());
        for (Header h : subheaders) Log.i(App.TAG, h.getTitle());
        // TODO penser à notifier l'Adapter depuis le UI Thread
    }

    void sync() {
        Log.i(App.TAG, "Synchronisation...");
        // Envoyer les listes d'actions en attente
        Pending.send(this, this.getContext());
        // Les méthodes de callback onSync ou onSyncNotAvailable seront ensuite appelées
    }

    void planNextSync() {
        Log.i(App.TAG, "test : plan next sync");
        // TODO On annule une éventuelle ancienne planification (Pour ne pas accumuler en cas de MAJ forcée)
        // Pour pouvoir annuler, passer de Timer à ScheduledExecutorService
        // Planification de la mise à jour
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sync();
            }
        }, 10000);
    }

    @Override
    public void onSync() {
        Log.i(App.TAG, "test : sync reussie");
        // En cas de réception de nouvelles données
        // On met à jour l'affichage de manière asynchrone
        SyncTask st = new SyncTask();
        st.execute();
        onPostSync();
    }

    @Override
    public void onSyncNotAvailable() {
        Log.i(App.TAG, "test : sync not available");
        // Quand une synchronisation se termine sans nouvelles données
        onPostSync();
    }

    protected void onPostSync() {
        planNextSync();
    }

    @Override
    public boolean isArchives() {
        return false;
    }

    List<Work> getHomeworks() {
        return this.homeworks;
    }

    protected void setHomeworks() {
        this.homeworks = Work.getComingwork(this.getContext());
    }

    List<Header> getHeaders() {
        return headers;
    }

    List<Header> getSubheaders() {
        return subheaders;
    }

    protected class SyncTask extends AsyncTask<Void, Integer, Void> {

        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(App.TAG, "test : doInBackground ");
            // TODO apply filters etc + supprimer Thread Sleep
            try {
                // TODO enlever
                // Permettre la visibilité de cette action (recalcsections) a priori très rapide et donc invisible
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recalcSections();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(WorkPage.this.getContext(), "Quelques instants", "Mise à jour en cours...");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.updateList(WorkPage.this);
            progressDialog.dismiss();
        }
    }
}

