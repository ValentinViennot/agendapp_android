package fr.agendapp.app.pages;


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

import ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderDecoration;
import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.factories.SyncFactory;
import fr.agendapp.app.listeners.ClassicListener;
import fr.agendapp.app.listeners.SyncListener;
import fr.agendapp.app.objects.FusionList;
import fr.agendapp.app.objects.Invite;
import fr.agendapp.app.utils.WrapLinearLayout;
import fr.agendapp.app.utils.pending.Pending;

/**
 * Page (Vue) d'affichage des devoirs à faire
 * Une Vue est un composant affichable dans une activité
 * Cette Vue est un "Fragment" car elle est utilisée dans une vue de type "ViewPager" (onglets)
 * @author Valentin Viennot
 */
public class WorkPage extends Fragment implements SyncListener {


    // Affichage de la liste de devoirs à fusionner entre eux
    public FusionList fusions;
    // Adapter permettant l'affichage de la liste de devoirs
    protected WorkAdapter adapter;
    // Affichage de la zone "invitations"
    private RecyclerView inviteView;
    // Affichage de la liste d'invitations
    private Invite.InviteAdapter inviteAdapter;
    // liste de devoirs
    private RecyclerView workList;
    // message en cas de hors connexion
    private TextView msgoffline;

    // Service de notification
    private NotificationFactory notificationFactory;

    // Nombre de synchronisations en attente
    private int planSync;
    // Délai de première synchronisation
    private int first_sync_delay = 700;
    // Positionnement à l'initialisation
    private static int pos=0;

    @Override // A la création de la Vue (page)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // On récupère la vue de la liste de devoirs que l'on va retourner
        View view = inflater.inflate(R.layout.activity_work, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            // Retarder la prochaine synchro (après un ajout par exemple)
            if (extras.containsKey("delay")) first_sync_delay = extras.getInt("delay");
            // Se diriger vers une position particuliere dans la liste
            if (extras.containsKey("pos")) pos = extras.getInt("pos");
            extras.clear();
        }

        // Remplissage de la vue

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
        //noinspection deprecation : Compatibilité Android 4+
        refresher.setColorSchemeColors(
                getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryDark)
        );

        // Section contenant la liste en elle même
        workList = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        // Instancie l'adapter permettant l'affichage de la liste de devoirs
        adapter = new WorkAdapter(this);
        // "Decoration" = en tetes
        DoubleHeaderDecoration decor = new DoubleHeaderDecoration(adapter);

        workList.setHasFixedSize(true);
        workList.setLayoutManager(new WrapLinearLayout(this.getActivity()));

        workList.addItemDecoration(decor);
        workList.setAdapter(adapter);

        notificationFactory = new NotificationFactory(this.getActivity());

        msgoffline = (TextView) view.findViewById(R.id.offlinemsg);
        msgoffline.setVisibility(View.GONE);

        planSync = 0;

        // Retourne la vue initialisée
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.update();
    }

    @Override // Lorsque l'onglet est fait visible (true) ou masqué (false)
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            // délai nécessaire au temps d'initialisation de la vue
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (workList != null) {
                        workList.smoothScrollToPosition(pos);
                        pos = 0;
                    }
                    // Autorise la synchronisation
                    planSync = 0;
                    // Lance une synchronisation des devoirs depuis le serveur
                    // La planification est inclue dans le callback onSync
                    sync();
                    // Récupère les invitations aux nouveaux groupes
                    getInvites();
                }
            }, first_sync_delay);
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
     * Lance une synchronisation
     * --> Envoi les listes d'attente au serveur
     * --> Récupère la version actuelle des devoirs sur le serveur
     * --> Compare à la version distante
     * --> Si besoin, télécharge la nouvelle version des données
     * --> En fonction de la situation, appelle la bonne fonction de callback
     * onSync() si de nouvelles données, onSyncNotAvailable() si pas de nouvelles données (ou pas internet)
     */
    void sync() {
        if (SyncFactory.isOffline() || SyncFactory.getServererror() != null) {
            msgoffline.setVisibility(View.VISIBLE);
            if (SyncFactory.isOffline())
                msgoffline.setText(R.string.code_offline);
            else
                msgoffline.setText(SyncFactory.getServererror());
            SyncFactory.checkServerStatus(this.getContext(), new ClassicListener() {
                @Override
                public void onCallBackListener() {
                    planNextSync();
                }
            });
        } else {
            msgoffline.setVisibility(View.GONE);
            // Envoyer les listes d'actions en attente
            // Enchaine automatiquement sur l'actualisation des données (voir méthode Pending.send())
            Pending.send(this, this.getContext(), notificationFactory);
            // Les méthodes de callback onSync ou onSyncNotAvailable seront ensuite appelées
            planNextSync();
        }
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
        adapter.update();
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