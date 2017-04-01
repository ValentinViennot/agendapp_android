package fr.agendapp.app.objects;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.agendapp.app.R;
import fr.agendapp.app.pages.CommentPage;

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
    private Attachment[] pjs;

    public Comment() {
        // TODO
    }

    /**
     * Supprimer le commentaire
     * - Supprimer localement du fil de commentaire
     * - Appel à l'API
     * Nécessite que l'utilisateur actuel en soit l'auteur
     *
     * @return true si réussite
     */
    public boolean delete() {
        if (User.getInstance().getId() == this.user) {
            // TODO
            return false;
        } else {
            // TODO ajout d'un message (Toast ?)
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public int getUser() {
        return user;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getText() {
        return texte;
    }

    public Attachment[] getPjs() {
        return pjs;
    }


    // Affichage des commentaires

    public static class CommentHolder extends RecyclerView.ViewHolder {

        private CommentPage.CommentAdapter adapter;
        private TextView text;

        public CommentHolder(LayoutInflater inflater, ViewGroup parent, CommentPage.CommentAdapter adapter) {
            super(inflater.inflate(R.layout.object_comment, parent, false));
            this.adapter = adapter;
            text = (TextView) itemView.findViewById(R.id.commenttext);
        }

        public void setComment(Comment c) {
            text.setText(c.getText());
        }
    }
}