package fr.agendapp.app.pages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import fr.agendapp.app.R;
import fr.agendapp.app.factories.DateFactory;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.filters.Filter;
import fr.agendapp.app.objects.Header;
import fr.agendapp.app.objects.Work;

/**
 * Adapteur pour l'affichage de la liste de devoirs
 */
class WorkAdapter extends RecyclerView.Adapter<WorkHolder> implements
        ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderAdapter<WorkAdapter.HeaderHolder, WorkAdapter.SubHeaderHolder> {

    // Lien vers la classe qui a instancié cet adapter
    private WorkPage workPage;

    // Liste de devoirs utilisée par l'adapter
    // Permet d'y appliquer le filtrage sans modifier la liste originelle
    private List<Work> homeworks;
    // Liste de headers utilisée par l'adapter
    private List<Header> headers;
    private HeaderHolder[] holders;
    // Liste d'en tetes de jour utilisée par l'adapter
    private List<Header> subheaders;
    private SubHeaderHolder[] subholders;

    /**
     * Création d'un nouvelle adapter
     *
     * @param workPage Classe qui créé cet adapter
     */
    WorkAdapter(WorkPage workPage) {
        // Effectue le lien entre l'adapter et la WorkPage
        this.workPage = workPage;
        // Initialise les listes, vides dans un premier temps
        homeworks = new LinkedList<>();
        headers = new LinkedList<>();
        subheaders = new LinkedList<>();
        // Liste de vues contenant les en tetes
        setHolders();
    }

    /**
     * Met à jour les vues contenant les en tetes
     * (utilisées plus tard pour forcer leur mise à jour)
     */
    private void setHolders() {
        this.subholders = new SubHeaderHolder[this.subheaders.size()];
        this.holders = new HeaderHolder[this.headers.size()];
    }

    /**
     * Mise à jour de l'adapter à partir de nouvelles données
     */
    void update() {
        new Refresh().execute();
    }

    /**
     * TODO : Problème soit dans le calcul des en tetes soit dans le calcul de l'ID d'en tete
     * Calcule les en-têtes et leur emplacement en fonction de la liste de devoirs actuelle
     */
    private void recalcSections() {
        // Réinitialisation des listes d'headers
        headers = new LinkedList<>();
        subheaders = new LinkedList<>();
        // Cas où la liste de devoirs est vide
        NotificationFactory.add(workPage.getActivity(), 0, "Pas de devoir à afficher", "");
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
                    month = new Header(i, DateFactory.getMonthName(workPage.getContext(), cal2.get(Calendar.MONTH)));
                    headers.add(month);
                }
                // Date différente => En tete de jour
                // Termine la section précédente
                if (day != null) {
                    day.setTo(i);
                }
                day = new Header(i, DateFactory.getWeekName(workPage.getContext(), cal2.get(Calendar.DAY_OF_WEEK)) + " " + cal2.get(Calendar.DAY_OF_MONTH));
                subheaders.add(day);
                // Met à jour la date de la section en cours
                cal.setTime(w.getDate());
            }
            i++;
        }
        if (month != null) month.setTo(i);
        if (day != null) day.setTo(i);
    }

    @Override
    public WorkHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Création d'un modèle de Vue pour les en tetes de mois
        return new WorkHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup, this);
    }

    @Override
    public void onBindViewHolder(WorkHolder holder, int position) {
        holder.setWork(homeworks.get(position));
    }

    @Override
    public int getItemCount() {
        return homeworks.size();
    }

    /**
     * Détermine l'ID (position dans la liste) d'un en tete à partir de la position du devoir auquel il est associé
     * Si les devoirs d'id 0,1,2 correspondent à l'en tete d'id 1 alors la méthode devra renvoyer
     * 1 pour le devoir 0
     * 1.33 pour le devoir 1
     * 1.66 pour le devoir 2
     *
     * @param position Position du devoir dans la liste
     * @param headers  Tableau d'en tetes
     * @return Position de l'en tete associé au devoir dans la liste headers
     */
    private long getLongId(int position, List<Header> headers) {
        int i = headers.size(), total, oldtotal = 1;
        ListIterator<Header> li = headers.listIterator(i);
        // Iteration dans le sens inversé
        while (li.hasPrevious()) {
            // Récupère la position+1 jusqu'à laquelle cet en tete va
            total = li.previous().getTo();
            // Si la position du devoir est supérieure ou égale c'est que le devoir appartenant à l'en tete précédent
            if (position >= total) {
                // On renvoit donc la position de l'en tete précédent (pas encore décrémenté)
                // + une fraction correspondant à
                // (la position du devoir) / ( (la position du dernier devoir de cet en tete ) +1 )
                return (i + (position) / oldtotal);
            }
            // Mémorise quel est le total de cet en tete
            oldtotal = total;
            i--;
        }
        return 0;
    }

    @Override
    public long getHeaderId(int position) {
        // Retourne la position de l'en tete de mois associé au devoir à cette position de la liste
        return getLongId(position, headers);
    }

    @Override
    public long getSubHeaderId(int position) {
        // Retourne la position de l'en tete de jour associé au devoir à cette position de la liste
        return getLongId(position, subheaders);
    }

    @Override
    public HeaderHolder onCreateHeaderHolder(ViewGroup parent) {
        // Création d'un modèle de Vue pour les en tetes de mois
        return new HeaderHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public SubHeaderHolder onCreateSubHeaderHolder(ViewGroup parent) {
        // Création d'un modèle de Vue pour les en tetes de jour
        return new SubHeaderHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindHeaderHolder(HeaderHolder viewholder, int position) {
        // Mise à jour de la vue de l'en tete de mois associé au devoir à cette position
        int id = (int) getHeaderId(position);
        viewholder.title.setText(headers.get(id).getTitle());
        holders[id] = viewholder;
    }

    @Override
    public void onBindSubHeaderHolder(SubHeaderHolder viewholder, int position) {
        // Récupère l'ID de l'en tete associé au devoir à cette position
        int id = (int) getSubHeaderId(position);
        // Met à jour le titre de la vue associée à cette position
        viewholder.title.setText(subheaders.get(id).getTitle());
        // Met à jour le lien vers la vue associée à l'en tête
        subholders[id] = viewholder;
    }

    private void updateHeaders() {
        for (int i = 0; i < subholders.length; ++i)
            if (subholders[i] != null)
                subholders[i].title.setText(subheaders.get(i).getTitle());

        for (int i = 0; i < holders.length; ++i)
            if (holders[i] != null)
                holders[i].title.setText(headers.get(i).getTitle());
    }

    void remove(Work w) {
        this.homeworks.remove(w);
    }

    Activity getActivity() {
        return this.workPage.getActivity();
    }

    boolean fusion(Work w) {
        return this.workPage.fusions.add(w);
    }

    private class Refresh extends AsyncTask<Void, Void, Void> {

        // Fenetre de dialogue de "chargement"
        ProgressDialog progressDialog;

        // Notifications de données modifiées
        private List<Integer> changed;
        private List<Integer> added;
        private List<Integer> removed;
        private List<Integer[]> moved;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Affiche une fenetre de chargement
            progressDialog = ProgressDialog.show(
                    workPage.getContext(),
                    workPage.getContext().getResources().getString(R.string.msg_wait),
                    workPage.getContext().getResources().getString(R.string.msg_updating)
            );
        }

        // Partie effectuée dans un autre Thread (Processus)
        @Override
        protected Void doInBackground(Void... params) {

            // TODO delete
            // Rend visible ce moment de calcul, a priori trop rapide pour etre visible
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Liste avant mise à jour (pour comparaison)
            List<Work> oldlist = homeworks;
            // holders avant mise à jour
            SubHeaderHolder[] tsh = subholders;
            HeaderHolder[] th = holders;

            // Récupère les nouvelles données (liste de devoir à jour = nouvelles références vers les objets devoirs)
            homeworks = workPage.isArchives() ?
                    Work.getPastwork(workPage.getActivity()) :
                    Work.getComingwork(workPage.getActivity());

            // Application des filtres à la liste de devoirs
            homeworks = Filter.applyFilters(homeworks);
            // Recalcule les sections en fonction de l'état de la nouvelle liste
            recalcSections();

            // On récupère des tableaux de holders de la taille des nouveaux tableaux d'en tete
            setHolders();
            // Récupère les anciennes références aux holders
            System.arraycopy(tsh, 0, subholders, 0, Math.min(tsh.length, subholders.length));
            System.arraycopy(th, 0, holders, 0, Math.min(th.length, holders.length));

            // Initialisation des listes de changements
            changed = new LinkedList<>();
            added = new LinkedList<>();
            removed = new LinkedList<>();
            moved = new LinkedList<>();

            // On compare l'ancienne liste à la nouvelle, et on note les changements dans les listes créées
            compare(oldlist, homeworks);
            // Les changements seront notifiés au Thread UI dans le post execute

            return null;
        }

        /**
         * Observe les modifications d'une nouvelle liste de données par rapport à une ancienne
         *
         * @param oldlist Ancienne liste
         * @param newlist Nouvelle liste
         */
        private void compare(List<Work> oldlist, List<Work> newlist) {
            ListIterator<Work> o = oldlist.listIterator();
            ListIterator<Work> n = newlist.listIterator();
            // Pour chaque element
            while (n.hasNext() && o.hasNext()) {
                Work cur_n = n.next();
                Work cur_o = o.next();
                // Si le devoir n'est pas le même que celui dans l'ancienne liste
                if (cur_n.getId() != cur_o.getId()) {
                    // On récupère sa position dans l'ancienne liste
                    int index = indexOf(cur_n, oldlist);
                    // S'il n'était pas dans l'ancienne liste, il s'agit d'une insertion
                    if (index < 0) added.add(n.previousIndex());
                        // Sinon, il s'agit d'un déplacement
                    else {
                        if (oldlist.get(index).modified(cur_n)) changed.add(n.previousIndex());
                        //if (index > n.previousIndex())
                            moved.add(new Integer[]{index, n.previousIndex()});
                    }
                    // On vérifie si l'élément de l'ancienne liste a été supprimé
                    if (indexOf(cur_o, newlist) < 0)
                        removed.add(o.previousIndex());// TODO o ou n ?
                } else {
                    // Si les devoirs sont égaux on regarde si un des paramètres variable a évolué
                    if (cur_o.modified(cur_n)) changed.add(n.previousIndex());
                }
            }
            // Pour chaque element restant de l'ancienne liste
            while (o.hasNext())
                // On regarde s'il a été supprimé
                if (indexOf(o.next(), newlist) < 0)
                    removed.add(o.previousIndex()); // TODO o ou n ?
            // Pour chaque element restant de la nouvelle liste
            int index;
            while (n.hasNext())
                // On regarde s'il était dans l'ancienne liste
                if ((index = indexOf(n.next(), oldlist)) < 0)
                    // si non, alors on insere
                    added.add(n.previousIndex());
                else
                    // si oui, alors on déplace
                    moved.add(new Integer[]{index, n.previousIndex()});
        }

        /**
         * @param list Liste de devoirs (Work)
         * @param e    Element Work
         * @return Index du devoir dans la liste (recherche par ID) ou -1 si non présent
         */
        private int indexOf(Work e, List<Work> list) {
            for (Work w : list)
                if (w.getId() == e.getId())
                    return list.indexOf(w);
            return -1;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for (Integer i : removed)
                notifyItemRemoved(i);
            for (Integer i : added)
                notifyItemInserted(i);
            for (Integer i : changed)
                notifyItemChanged(i);
            for (Integer[] i : moved) {
                for (Integer[] j : moved) {
                    if (i[0].equals(j[1]) && j[0].equals(i[1])) {
                        notifyItemChanged(i[0]);
                        notifyItemChanged(i[1]);
                        //notifyItemMoved(i[0], i[1]);
                    }
                }
            }

            /// DEBUG
            /*for (Integer i : removed)
                Log.i(App.TAG,"removed "+i);
            for (Integer i : added)
                Log.i(App.TAG,"added "+i);
            for (Integer i : changed)
                Log.i(App.TAG,"changed "+i);
            for (Integer[] i : moved) {
                for (Integer[] j : moved) {
                    if (i[0].equals(j[1]) && j[0].equals(i[1])) {
                        Log.i(App.TAG,"removed "+i[0]);
                        Log.i(App.TAG,"removed "+i[1]);
                        //notifyItemMoved(i[0], i[1]);
                    }
                }
            }*/

            updateHeaders();

            progressDialog.dismiss();
        }
    }

    /**
     * Vue pour un en tete de mois
     */
    class HeaderHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.object_header, parent, false));
            title = (TextView) itemView.findViewById(R.id.headertitle);
        }
    }

    /**
     * Vue pour un en tete de jour
     */
    class SubHeaderHolder extends RecyclerView.ViewHolder {
        TextView title;

        SubHeaderHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.subheader, parent, false));
            title = (TextView) itemView.findViewById(R.id.subheadertitle);
        }
    }
}
