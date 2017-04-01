package fr.agendapp.app.pages;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderDecoration;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.factories.SyncFactory;
import fr.agendapp.app.listeners.ClassicListener;
import fr.agendapp.app.listeners.SyncListener;
import fr.agendapp.app.objects.FusionList;
import fr.agendapp.app.objects.Invite;
import fr.agendapp.app.pending.Pending;

/**
 * TODO passer les protected qui le peuvent en private (rappel : protected donne la visibilité à la classe et des classes filles)
 * Page (Vue) d'affichage des devoirs à faire
 * Une Vue est un composant affichable dans une activité
 * Cette Vue est un "Fragment" car elle est utilisée dans une vue de type "ViewPager" (onglets)
 */
public class WorkPage extends Fragment implements SyncListener {


    // Affichage de la liste de devoirs à fusionner entre eux
    public FusionList fusions;
    /*    // Liste de devoirs
        protected List<Work> homeworks;
        // Liste d'en tetes (mois) liée à la liste de devoirs
        protected List<Header> headers;
        // Liste d'en tetes (jour) liée à la liste de devoirs
        protected List<Header> subheaders;*/
    // Adapter permettant l'affichage de la liste de devoirs
    protected WorkAdapter adapter;
    // Affichage de la zone "invitations"
    private RecyclerView inviteView;
    // Affichage de la liste d'invitations
    private Invite.InviteAdapter inviteAdapter;

    private NotificationFactory notificationFactory;

    // Nombre de synchronisations en attente
    private int planSync = 0;

    @Override // A la création de la Vue (page)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // On récupère la vue de la liste de devoirs que l'on va retourner
        View view = inflater.inflate(R.layout.activity_work, container, false);

        // Remplissage de la vue

        // Bouton d'action flottant (pour ajouter un devoir)
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WorkPage.this.getActivity(), NewPage.class));
            }
        });

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

        // Tirer la vue vers le bas pour mettre à jour les données
        final SwipeRefreshLayout refresher = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SyncFactory.getInstance(WorkPage.this.getContext())
                        // mise à jour avec notifications en cas d'erreur
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
        adapter = new WorkAdapter(this);
        // "Decoration" = en tetes
        DoubleHeaderDecoration decor = new DoubleHeaderDecoration(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(adapter);

        notificationFactory = new NotificationFactory(this.getActivity());

        // Retourne la vue initialisée
        return view;
    }

    @Override // Au démarrage de l'activité (ou sa reprise)
    public void onStart() {
        super.onStart();
        // On actualise l'affichage des devoirs à partir des données disponibles localement
        // dès le démarrage pour un affichage aussi rapide que possible
        this.refresh();
    }

    @Override // Lorsque l'onglet est fait visible (true) ou masqué (false)
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            planSync = 0;
            // Lance une synchronisation des devoirs depuis le serveur
            // La planification est inclue dans le callback onSync
            this.sync();
            // Invitations (délai nécessaire au temps d'initialisation de la vue des invitations)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Récupère les invitations aux nouveaux groupes
                    getInvites();
                }
            }, 100);
        } else {
            // Rend impossible le lancement d'autres instances de synchronisation
            planSync++;
        }
    }

    /**
     * Récupère les <code>Invite</code> (invitations) depuis le serveur
     */
    private void getInvites() {
        if (inviteView != null) {
            // On masque la vue en attendant de , peut être, recevoir les invitations
            inviteView.setVisibility(View.GONE);
            // Récupère les invitations
            SyncFactory.getInstance(getContext()).getInvites(getContext(), new ClassicListener() {
                @Override
                public void onCallBackListener() {
                    // Les invitations sont stockées dans la classe Invite
                    // On met à jour l'adapter (gestionnaire de liste)
                    inviteAdapter.notifyDataSetChanged();
                    // S'il y a effectivement des invitations
                    if (inviteAdapter.getItemCount() > 0)
                        // On affiche la vue
                        inviteView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * Recalculer l'affichage dans un Thread séparé puis le mettre à jour
     */
    private void refresh() {
        adapter.update();
    }

    /**
     * Lance une synchronisation
     * --> Envoi les listes d'attente au serveur
     * --> Récupère la version actuelle des devoirs sur le serveur
     * --> Compare à la version distante
     * --> Si besoin, télécharge la nouvelle version des données
     * --> En fonction de la situation, appelle la bonne fonction de callback
     * onSync() si de nouvelles données, onSyncNotAvailable() si pas de nouvelles données (ou pas internet)
     */
    void sync() {
        // Envoyer les listes d'actions en attente
        // Enchaine automatiquement sur l'actualisation des données (voir méthode Pending.send())
        Pending.send(this, this.getContext(), notificationFactory);
        // Les méthodes de callback onSync ou onSyncNotAvailable seront ensuite appelées
        planNextSync();
    }

    /**
     * Planification d'une prochaine synchronisation dans SYNC_DELAY secondes
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
    }

    @Override
    public void onSyncNotAvailable() {
        // Quand une synchronisation se termine sans nouvelles données
    }

    @Override
    public boolean isArchives() {
        return false;
    }


}