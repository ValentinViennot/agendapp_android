package fr.agendapp.app.objects;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.R;
import fr.agendapp.app.pages.CommentPage;
import fr.agendapp.app.utils.pending.PendDELc;

import static android.view.View.GONE;

/**
 * Commentaire
 *
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Comment {

    /**
     * ID du commentaire dans la base
     */
    private int id;
    /**
     * ID de l'auteur
     */
    private int user;
    /**
     * "@prenomnom" de l'auteur
     */
    private String auteur;
    /**
     * texte du commentaire
     */
    private String texte;
    /**
     * Pièces jointes attachées au commentaire
     */
    private List<Attachment> pjs;
    /**
     * Date d'ajout du devoir
     */
    private Date date;

    /**
     * Default
     * Contructeur pour librairie GSON
     */
    public Comment() {
    }

    /**
     * Contructeur local
     *
     * @param text Texte du commentaire
     */
    public Comment(String text) {
        User user = User.getInstance();
        // ID impossible <=> non publié sur le serveur
        this.id = -1;
        this.user = user.getId();
        this.date = new Date();
        this.pjs = new LinkedList<>();
        this.texte = text;
        this.auteur = user.getPrenom() + user.getNom();
    }

    /**
     * Supprimer le commentaire
     * - Supprimer localement du fil de commentaire
     * - Appel à l'API
     * Nécessite que l'utilisateur actuel en soit l'auteur
     */
    private void delete(Context context, Work w) {
        // Supprime de la liste de commentaires du devoir
        w.getComments().remove(this);
        // Requete au serveur
        new PendDELc(context, this);
        // Sauvegarde la liste de devoirs locale
        Work.saveList(context);
    }

    public int getId() {
        return id;
    }

    public int getUser() {
        return user;
    }

    private String getAuteur() {
        return auteur;
    }

    public String getText() {
        return texte;
    }

    public Date getDate() {
        return date;
    }

    private List<Attachment> getAttchments() {
        return pjs;
    }


    // Affichage des commentaires

    /**
     * Affichage (UI) d'un commentaire
     */
    public static class CommentHolder extends RecyclerView.ViewHolder {

        private LayoutInflater inflater;
        private Resources r;
        private CommentPage.CommentAdapter adapter;
        private TextView text;
        private TextView meta;
        private ImageButton delete;
        private GridView comments;

        public CommentHolder(LayoutInflater inflater, ViewGroup parent, CommentPage.CommentAdapter adapter) {
            super(inflater.inflate(R.layout.object_comment, parent, false));
            this.inflater = inflater;
            this.adapter = adapter;
            this.r = inflater.getContext().getResources();
            text = (TextView) itemView.findViewById(R.id.commenttext);
            meta = (TextView) itemView.findViewById(R.id.commentauthor);
            delete = (ImageButton) itemView.findViewById(R.id.commentdel);
            comments = (GridView) itemView.findViewById(R.id.commentattachments);
        }

        /**
         * @param c Commentaire à afficher sur la vue (recyclée)
         */
        public void setComment(final Comment c) {
            text.setText(Html.fromHtml(c.getText()));
            Calendar cal = Calendar.getInstance();
            cal.setTime(c.getDate());
            String d = (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + cal.get(Calendar.DAY_OF_MONTH) + "/"
                    + (cal.get(Calendar.MONTH) < 10 ? "0" : "") + cal.get(Calendar.MONTH);
            d += " ";
            d += cal.get(Calendar.HOUR_OF_DAY) + ":"
                    + (cal.get(Calendar.MINUTE) < 10 ? "0" : "") + cal.get(Calendar.MINUTE);
            meta.setText(r.getString(R.string.comment_meta, c.getAuteur(), d));
            if (User.getInstance().getId() != c.getUser() || c.getId() < 0)
                delete.setVisibility(GONE);
            else
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // On effectue la requête de suppression du commentaire au serveur
                        c.delete(inflater.getContext(), adapter.getWork());
                        // Mise à jour de l'affichage
                        adapter.notifyItemRemoved(getAdapterPosition());
                    }
                });
            // Pièces jointes
            if (c.getAttchments().size() > 0)
                comments.setAdapter(new Attachment.AttachmentAdapter(c.getAttchments(), inflater));
            else
                comments.setVisibility(GONE);
        }
    }
}