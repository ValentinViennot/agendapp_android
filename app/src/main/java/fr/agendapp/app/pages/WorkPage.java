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
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.factories.Pending;
import fr.agendapp.app.objects.Filter;
import fr.agendapp.app.objects.Header;
import fr.agendapp.app.objects.Work;

/**
 * TODO passer les protected qui le peuvent en private (rappel : protected donne la visibilité à la classe et des classes filles)
 * Page (Vue) d'affichage des devoirs à faire
 * Une Vue est un composant affichable dans une activité
 * Cette Vue est un "Fragment" car elle est utilisée dans une vue de type "ViewPager" (onglets)
 */
public class WorkPage extends Fragment implements SyncListener {

    // Nombre de filtre maximal par catégorie de filtre
    private final int MAX_FILTERS = 5;
    // Liste d'en tetes (mois) liée à la liste de devoirs
    protected List<Header> headers;
    // Liste d'en tetes (jour) liée à la liste de devoirs
    protected List<Header> subheaders;
    // Liste de devoirs
    protected List<Work> homeworks;
    // Adapter permettant l'affichage de la liste de devoirs
    protected DoubleHeaderAdapter adapter;
    // TODO Timer devrait être déprécié... Remplacer par ScheduledExecutorService (pas urgent)
    // Timer utilisé pour l'actualisation régulière des données
    protected Timer timer = new Timer();
    // tableau 2D de filtres
    // Filter[] chaque case de ce tableau doit être vérifiée
    // Filter[i][] chaque case du sous tableau correspond à un Filter (filtre)
    // Il suffit qu'une seule condition j de Filter[i][j] soit validée pour que la condition i soit validée
    Filter[][] filters = new Filter[Filter.NB_TYPES][MAX_FILTERS];

    @Override // A la création de la Vue (page)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        headers = new LinkedList<>();
        subheaders = new LinkedList<>();
        homeworks = new LinkedList<>();
        // On récupère la vue dans laquelle seront affiché les devoirs
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        // Instancie l'adapter permettant l'affichage de la liste de devoirs
        adapter = new DoubleHeaderAdapter(this);
        // "Decoration" = en tetes
        DoubleHeaderDecoration decor = new DoubleHeaderDecoration(adapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        recyclerView.addItemDecoration(decor);
        recyclerView.setAdapter(adapter);

        // Retourne la vue initialisée
        return recyclerView;
    }

    @Override // Au démarrage de l'activité (ou sa reprise)
    public void onStart() {
        super.onStart();
        // On actualise l'affichage des devoirs à partir des données disponibles localement
        // dès le démarrage pour un affichage aussi rapide que possible
        SyncTask st = new SyncTask();
        st.execute();
        // Lance une synchronisation des devoirs depuis le serveur
        // La planification est inclue dans le callback onSync
        this.sync();
    }
    //TODO onStop qui arrête le timer

    /**
     * TODO : Problème soit dans le calcul des en tetes soit dans le calcul de l'ID d'en tete
     * Calcule les en-têtes et leur emplacement en fonction de la liste de devoirs
     */
    protected void recalcSections() {
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
    }

    // TODO implémenter le filtrage avant le recalcSection
    // TODO crée une vue permettant de filtrer puis bouton ==> recalc affichage
    /*List<Work> applyFilters2() {
        List<Work> r;
        // Pour chaque devoir
        for (Work w : homeworks) {
            if (validateFilters(w)) r.add(w);
        }
    }*/

    /**
     * @return true si le devoir valide les conditions Filter[][]
     */
    boolean validateFilters(Work w) {
        // Pour chaque groupe de filtres
        for (int i = 0; i < filters.length; ++i) {
            if (filters[i] != null) {
                // Le devoir doit correspondre à au moins un filtre du groupe
                // On suppose que c'est faux
                boolean b = false;
                int j = 0;
                // Tant que c'est faux, on continue d'essayer de le montrer
                // Jusqu'à avoir testé tous les filtres du groupe
                while (!b && j < filters[i].length) {
                    // Si le devoir correspond au filtre
                    // b vaudra true
                    // la boucle s'arrêtera
                    // et on testera le groupe suivant
                    if (filters[i][j] != null)
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
        for (int i = 0; i < filters[filter.getType()].length; ++i) {
            // On cherche la première case nulle
            if (filters[filter.getType()][i] == null) {
                // On y ajoute le filtre
                filters[filter.getType()][i] = filter;
                // Quitte la méthode
                return;
            }
        }
        NotificationFactory.add(this.getActivity(), 1, "Impossible", "Trop de filtres appliqués !");
    }

    void clearFilter(Filter filter) {
        filter = null;
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
        Log.i(App.TAG, "Synchronisation...");
        // Envoyer les listes d'actions en attente
        // Enchaine automatiquement sur l'actualisation des données (voir méthode send de Pending)
        Pending.send(this, this.getContext());
        // Les méthodes de callback onSync ou onSyncNotAvailable seront ensuite appelées
    }

    /**
     * Planification d'une prochaine synchronisation dans x secondes
     */
    void planNextSync() {
        // TODO Annuler une éventuelle ancienne planification (Pour ne pas accumuler en cas de MAJ forcée)
        // Planification de la mise à jour TODO ajuster la fréquence de synchro
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sync();
            }
        }, 5000);
    }

    @Override
    public void onSync() {
        // En cas de réception de nouvelles données
        // On met à jour l'affichage de manière asynchrone
        SyncTask st = new SyncTask();
        st.execute();
        onPostSync();
    }

    @Override
    public void onSyncNotAvailable() {
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
    protected class SyncTask extends AsyncTask<Void, Integer, Void> {

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
            progressDialog = ProgressDialog.show(WorkPage.this.getContext(), "Quelques instants", "Mise à jour en cours...");
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