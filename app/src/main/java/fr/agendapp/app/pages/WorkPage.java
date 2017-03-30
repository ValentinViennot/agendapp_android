package fr.agendapp.app.pages;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderDecoration;
import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.DateFactory;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.factories.Pending;
import fr.agendapp.app.factories.SyncFactory;
import fr.agendapp.app.listeners.ClassicListener;
import fr.agendapp.app.listeners.SyncListener;
import fr.agendapp.app.objects.FusionList;
import fr.agendapp.app.objects.Header;
import fr.agendapp.app.objects.Invite;
import fr.agendapp.app.objects.Work;
import fr.agendapp.app.utils.Filter;

/**
 * TODO passer les protected qui le peuvent en private (rappel : protected donne la visibilité à la classe et des classes filles)
 * Page (Vue) d'affichage des devoirs à faire
 * Une Vue est un composant affichable dans une activité
 * Cette Vue est un "Fragment" car elle est utilisée dans une vue de type "ViewPager" (onglets)
 */
public class WorkPage extends Fragment implements SyncListener {

    // Affichage de la liste de devoirs à fusionner entre eux
    public FusionList fusions;
    // Liste de devoirs
    protected List<Work> homeworks;
    // Liste d'en tetes (mois) liée à la liste de devoirs
    protected List<Header> headers;
    // Liste d'en tetes (jour) liée à la liste de devoirs
    protected List<Header> subheaders;
    // Adapter permettant l'affichage de la liste de devoirs
    protected DoubleHeaderAdapter adapter;
    // Affichage de la zone "invitations"
    private RecyclerView inviteView;
    // Affichage de la liste d'invitations
    private Invite.InviteAdapter inviteAdapter;

    // Nombre de synchronisations en attente
    private int planSync = 0;

    @Override // A la création de la Vue (page)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        headers = new LinkedList<>();
        subheaders = new LinkedList<>();
        homeworks = new LinkedList<>();

        // On récupère la vue de la liste de devoirs
        View view = inflater.inflate(R.layout.activity_work, container, false);

        // Section contenant la liste de fusion
        fusions = new FusionList(
                (CardView) view.findViewById(R.id.fusion_view),
                (TextView) view.findViewById(R.id.fusion_1),
                (TextView) view.findViewById(R.id.fusion_2),
                (TextView) view.findViewById(R.id.fusion_3)
        );

        // Section contenant les invitations à des groupes
        inviteView = (RecyclerView) view.findViewById(R.id.view_invitations);
        inviteAdapter = new Invite.InviteAdapter();
        inviteView.setHasFixedSize(false);
        inviteView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        inviteView.setAdapter(inviteAdapter);

        final SwipeRefreshLayout refresher = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SyncFactory.getInstance(WorkPage.this.getContext())
                        .getVersion(WorkPage.this, WorkPage.this.getContext(), new NotificationFactory(WorkPage.this.getActivity()));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresher.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        refresher.setColorSchemeColors(
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark));

        // Section contenant la liste en elle même
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        // Instancie l'adapter permettant l'affichage de la liste de devoirs
        adapter = new DoubleHeaderAdapter(this);
        // "Decoration" = en tetes
        DoubleHeaderDecoration decor = new DoubleHeaderDecoration(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(adapter);

        // Retourne la vue initialisée
        return view;
    }

    @Override // Au démarrage de l'activité (ou sa reprise)
    public void onStart() {
        super.onStart();
        Log.i(App.TAG, "onStart (" + (isArchives() ? "A" : "D") + ")");
        // On actualise l'affichage des devoirs à partir des données disponibles localement
        // dès le démarrage pour un affichage aussi rapide que possible
        this.refresh();
        // Lance une première synchronisation forcée
        this.sync();
        // TODO premier appel non efficace (nécessite de re rentrer dans la vue, même choe pour sync ? :o )
        getInvites();
    }

    @Override // Lorsque l'onglet est fait visible (true) ou masqué (false)
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            planSync = 0;
            // Lance une synchronisation des devoirs depuis le serveur
            // La planification est inclue dans le callback onSync
            this.sync();
        } else {
            planSync++;
        }
    }

    private void getInvites() {
        // On masque la vue en attendant de , peut être, recevoir les invitations
        inviteView.setVisibility(View.GONE);
        // Récupère les invitations
        SyncFactory.getInstance(getContext()).getInvites(getContext(), new ClassicListener() {
            @Override
            public void onCallBackListener() {
                Log.i(App.TAG, "invitations reçues");
                inviteAdapter.notifyDataSetChanged();
                if (inviteAdapter.getItemCount() > 0)
                    inviteView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Recalculer l'affichage dans un Thread séparé puis le mettre à jour
     */
    private void refresh() {
        SyncTask st = new SyncTask();
        st.execute();
    }

    /**
     * TODO : Problème soit dans le calcul des en tetes soit dans le calcul de l'ID d'en tete
     * Calcule les en-têtes et leur emplacement en fonction de la liste de devoirs
     */
    protected void recalcSections() {
        // Réinitialisation de la liste de devoirs
        setHomeworks();
        //TODO
        //Filter.addFilter(new FilterDone(false));
        // Application des filtres à la liste de devoirs
        this.homeworks = Filter.applyFilters(this.homeworks);
        // Réinitialisation des listes d'headers
        headers = new LinkedList<>();
        subheaders = new LinkedList<>();
        // Cas où la liste de devoirs est vide
        // TODO Affichage d'un message
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
    }

    /**
     * Lance une sycnhronisation
     * --> Envoi les listes d'attente au serveur
     * --> Récupère la version actuelle des devoirs sur le serveur
     * --> Compare à la version distante
     * --> Si besoin, télécharge la nouvelle version des données
     * --> En fonction de la situation, appelle la bonne fonction de callback
     * onSync() si de nouvelles données, onSyncNotAvailable() si pas de nouvelles données (ou pas internet)
     */
    void sync() {
        Log.i(App.TAG, "SYNC " + (isArchives() ? "A" : "D"));
        // Envoyer les listes d'actions en attente
        // Enchaine automatiquement sur l'actualisation des données (voir méthode send de Pending)
        Pending.send(this, this.getContext(), new NotificationFactory(this.getActivity()));
        // Les méthodes de callback onSync ou onSyncNotAvailable seront ensuite appelées
        planNextSync();
    }

    /**
     * Planification d'une prochaine synchronisation dans x secondes
     */
    protected void planNextSync() {
        int SYNC_DELAY = 2000;
        planNextSync(SYNC_DELAY);
    }

    /**
     * @param delay Delai entre chaque synchronisation
     */
    protected void planNextSync(int delay) {
        if (planSync == 0) {
            planSync++;
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            planSync--;
                            sync();
                        }
                    }, delay
            );
        }
    }

    @Override
    public void onSync() {
        // En cas de réception de nouvelles données
        // On met à jour l'affichage de manière asynchrone
        refresh();
        onPostSync();
    }

    @Override
    public void onSyncNotAvailable() {
        // Quand une synchronisation se termine sans nouvelles données
        onPostSync();
    }

    // TODO : Pas mal de méthodes vides par ici...

    protected void onPostSync() {

    }

    @Override
    public boolean isArchives() {
        return false;
    }

    List<Work> getHomeworks() {
        return this.homeworks;
    }

    List<Header> getHeaders() {
        return headers;
    }

    List<Header> getSubheaders() {
        return subheaders;
    }

    /**
     * Met à jour la liste utilisée parla Vue de devoirs avec celle enregistrée localement
     */
    protected void setHomeworks() {
        this.homeworks = Work.getComingwork(this.getContext());
    }

    /**
     * Thread de clacul à appeler après une synchronisation et ou avant l'affichage des données
     * Cette classe interne permet de créer un processus séparé du processus principal dans lequel
     * seront retriées les nouvelles listes de données, les headers, etc.
     */
    private class SyncTask extends AsyncTask<Void, Integer, Void> {

        // Fenetre de dialogue de "chargement"/"loading"/spinner
        ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... params) {
            // TODO apply filters, MAJ de la liste locale avec celle distante (plutot dans un autre Thread avant l'appel de onSync())
            try {
                // TODO enlever , utile pour le debogage
                // Permettre la visibilité de cette action (recalcsections) a priori très rapide et donc invisible lors de la simulation
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Recalcule les sections de devoirs (en tetes de jour et de mois)
            recalcSections();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Affiche une fenetre de chargement
            progressDialog = ProgressDialog.show(
                    WorkPage.this.getContext(),
                    WorkPage.this.getContext().getResources().getString(R.string.msg_wait),
                    WorkPage.this.getContext().getResources().getString(R.string.msg_updating)
            );
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Notifie l'adapter que la liste de devoirs vient d'être mise à jour
            // TODO ne notifier que là où il y a eu des changements
            adapter.updateList(WorkPage.this);
            // Masque la fenetre de chargement
            progressDialog.dismiss();
        }
    }

}