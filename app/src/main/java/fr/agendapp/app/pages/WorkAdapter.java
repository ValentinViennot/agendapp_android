package fr.agendapp.app.pages;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.objects.Attachment;
import fr.agendapp.app.objects.FusionList;
import fr.agendapp.app.objects.Header;
import fr.agendapp.app.objects.Work;

/**
 * Adapteur pour l'affichage de la liste de devoirs
 */
class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> implements
        ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderAdapter<WorkAdapter.HeaderHolder, WorkAdapter.SubHeaderHolder> {

    private Activity activity;
    // Liste de devoirs utilisée par l'adapter
    private List<Work> homeworks;
    // Liste de headers utilisée par l'adapter
    private List<Header> headers;
    private HeaderHolder[] holders;
    // Liste d'en tetes de jour utilisée par l'adapter
    private List<Header> subheaders;
    private SubHeaderHolder[] subholders;

    // Lien vers la liste de fusion (pour interaction avec)
    private FusionList fusionList;

    WorkAdapter(WorkPage wp) {
        initData(wp);
    }

    private void initData(WorkPage wp) {
        this.homeworks = wp.getHomeworks();

        this.headers = wp.getHeaders();
        this.subheaders = wp.getSubheaders();

        this.fusionList = wp.fusions;
        this.activity = wp.getActivity();

        this.subholders = new SubHeaderHolder[this.subheaders.size()];
        this.holders = new HeaderHolder[this.headers.size()];
    }

    void updateList(WorkPage wp) {
        // Liste avant mise à jour (pour comparaison)
        List<Work> oldlist = this.homeworks;
        // holders avant comparaison
        SubHeaderHolder[] tsh = subholders;
        HeaderHolder[] th = holders;
        // Nouvelles données
        initData(wp);
        // Récupère les anciennes références aux holders
        for (int i = 0; i < Math.min(tsh.length, subholders.length); ++i)
            subholders[i] = tsh[i];
        for (int i = 0; i < Math.min(th.length, holders.length); ++i)
            holders[i] = th[i];
        // Modifie l'affichage uniquement où il a été modifié
        new NotifyChanges()
                .execute(oldlist, this.homeworks);
    }

    @Override
    public WorkAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Création d'un modèle de Vue pour les en tetes de mois
        return new WorkAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
    }

    @Override
    public void onBindViewHolder(WorkAdapter.ViewHolder holder, int position) {
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

    /**
     * Définition de l'affichage d'un devoir (UI)
     * Quels widgets sont nécessaires pour l'affichage ?
     * Comment sont affichées les données , réactions au clic etc
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private LayoutInflater inflater;


        private TextView subject;
        private TextView text;
        private ImageButton flag;
        private TextView nbDone;
        private TextView nbComm;
        private Button done;
        private ImageButton menu;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.object_work, parent, false));
            this.inflater = inflater;

            subject = (TextView) itemView.findViewById(R.id.card_subject);
            text = (TextView) itemView.findViewById(R.id.card_text);
            flag = (ImageButton) itemView.findViewById(R.id.card_flag);
            nbDone = (TextView) itemView.findViewById(R.id.card_nbDone);
            nbComm = (TextView) itemView.findViewById(R.id.card_nbComment);
            done = (Button) itemView.findViewById(R.id.button_done);//TODO marche pas après 1 synchro au premier clic (pas de MAJ affichage)
            menu = (ImageButton) itemView.findViewById(R.id.more_button);

        }

        /**
         * Méthode appelée à chaque fois qu'un devoir est affiché / actualisé
         * Code exécuté dans le Thread UI
         * (d'où la necessité de ne pas mettre à jour toute la liste à chaque MAJ)
         *
         * @param w Devoir
         */
        public void setWork(final Work w) {

            final Context context = inflater.getContext();
            final Resources r = context.getResources();

            // Matière
            subject.setText(w.getSubject());
            if (w.isDone())
                subject.setPaintFlags(subject.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            else subject.setPaintFlags(text.getPaintFlags());
            subject.setTextColor(w.getSubjectColor());

            // Texte du devoir
            text.setText(Html.fromHtml(w.getText()));

            // Pièces jointes
            GridView gridview = (GridView) itemView.findViewById(R.id.card_attachments);
            gridview.setAdapter(new Attachment.AttachmentAdapter(w.getAttachments(), inflater));

            // Drapeau / Marqueur
            int color;
            switch (w.getFlag()) {
                case 1:
                    // Bleu
                    color = Color.parseColor("#4178BE");
                    break;
                case 2:
                    // Orange
                    color = Color.parseColor("#FF7832");
                    break;
                case 3:
                    // Rouge
                    color = Color.parseColor("#E71D32");
                    break;
                default:
                    // Gris
                    color = Color.parseColor("#999999");
            }
            flag.setColorFilter(color);

            // Sélection d'un marqueur
            flag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.flags_title)
                            .setItems(R.array.flags, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(App.TAG, "flag : " + which);
                                    w.setFlag(context, which);
                                    notifyItemChanged(getAdapterPosition());
                                }
                            });
                    builder.create().show();
                }
            });

            // Menu
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, v);
                    final MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.work_menu, popup.getMenu());
                    MenuItem done = popup.getMenu().findItem(R.id.menu_done);
                    done.setTitle(w.isDone() ? R.string.button_undone : R.string.button_done);
                    MenuItem delete = popup.getMenu().findItem(R.id.menu_delete);
                    delete.setTitle(w.isUser() ? R.string.button_delete : R.string.button_alert);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_done:
                                    w.done(context);
                                    notifyItemChanged(getAdapterPosition());
                                    return true;
                                case R.id.menu_delete:
                                    if (w.isUser())
                                        w.delete(context);
                                    else
                                        w.report(context);
                                    WorkAdapter.this.homeworks.remove(w);
                                    notifyItemRemoved(getAdapterPosition());
                                    // TODO Décalage du devoir suivant celui supprimé dans le header du dessus (problème de bornes, recalcsections)
                                    updateHeaders();
                                    return true;
                                case R.id.menu_fusion:
                                    if (!fusionList.add(w)) {
                                        NotificationFactory.add(activity, 1, r.getString(R.string.msg_impossible), r.getString(R.string.msg_fusionimpossible));
                                    }
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });

            // Footer
            String nb = "" + w.getNbDone();
            nbDone.setText(nb);
            nb = "" + w.getComments().size();
            nbComm.setText(nb);

            if (w.isDone())
                done.setText(r.getString(R.string.button_undone));
            else
                done.setText(r.getString(R.string.button_done));

            // Boutons
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    w.done(context);
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }

    private class NotifyChanges extends AsyncTask<List<Work>, Void, Void> {

        private List<Integer> changed;
        private List<Integer> added;
        private List<Integer> removed;
        private List<Integer[]> moved;

        // Partie effectuée dans un autre Thread (Processus)
        @Override
        protected Void doInBackground(List<Work>... params) {
            // Initialisation des listes
            changed = new LinkedList<>();
            added = new LinkedList<>();
            removed = new LinkedList<>();
            moved = new LinkedList<>();
            List<Work> oldlist = params[0];
            List<Work> newlist = params[1];
            // On compare la liste 1 à la liste 2
            compare(oldlist, newlist);
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
                        removed.add(o.previousIndex());//TODO o ou n index ?
                } else {
                    // Si les devoirs sont égaux on regarde si un des paramètres variable a évolué
                    if (cur_o.modified(cur_n)) changed.add(n.previousIndex());
                }
            }
            // Pour chaque element restant de l'ancienne liste
            while (o.hasNext())
                // On regarde s'il a été supprimé
                if (indexOf(o.next(), newlist) < 0)
                    removed.add(o.previousIndex());//TODO o ou n index ?
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

        }
    }
}
