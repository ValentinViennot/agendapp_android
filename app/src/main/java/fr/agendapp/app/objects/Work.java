package fr.agendapp.app.objects;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import fr.agendapp.app.R;

/**
 * Représente un devoir
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Work {

    /** ID dans la base */
    private int id;
    /** ID de l'auteur */
    private int user;
    /** "@prenomnom" de l'auteur */
    private String auteur;
    /** Nom de la matière */
    private String matiere;
    /** Couleur associée la matière */
    private Color matiere_c;
    /** Texte du devoir */
    private String texte;
    /** Date d'échéance */
    private Date date;
    /** Nombre de marqué comme faits */
    private int nb_fait;
    /** Utilisateur a marqué comme fait ? */
    private boolean fait;
    /** Drapeau attaché par l'utilisateur */
    private int flag;
    /** Liste de commentaires */
    private ArrayList<Comment> commentaires;
    /** Liste de pièces jointes */
    private ArrayList<Attachment> pjs;

    public Work(String auteur) {
        this.auteur = auteur;
    }

    /**
     * Marque comme fait/non fait selon le statut actuel
     * @return true si le devoir est marqué comme fait par l'utilisateur, false sinon
     */
    boolean done() {
        // TODO
        return false;
    }

    // GETTERS

    /**
     * Supprime le devoir
     * @return true si le devoir est supprimé par l'utilisateur, false sinon
     */
    boolean delete() {
        // TODO
        return false;
    }

    /**
     * Signale le devoir au modérateur
     * @return true si le devoir est signalé par l'utilisateur, false sinon
     */
    boolean report() {
        //TODO
        return false;
    }

    // GETTERS
    // TODO getters en doubles !!!

    public int getId() {
        return id;
    }

    public int getUser() {
        return user;
    }

    public String getAuthor() {
        return auteur;
    }

    public String getSubject() {
        return matiere;
    }

    public Color getSubjectColor() {
        return matiere_c;
    }

    public String getText() {
        return texte;
    }

    public Date getDate() {
        return date;
    }

    public int getNbDone() {
        return nb_fait;
    }

    public boolean isDone() {
        return fait;
    }

    public int getFlag() {
        return flag;
    }

    public ArrayList<Comment> getComments() {
        return commentaires;
    }

    public ArrayList<Attachment> getAttachments() {
        return pjs;
    }


    public String getAuteur() {
        return auteur;
    }

    public String getMatiere() {
        return matiere;
    }

    public Color getMatiere_c() {
        return matiere_c;
    }

    public String getTexte() {
        return texte;
    }

    public int getNb_fait() {
        return nb_fait;
    }

    public boolean isFait() {
        return fait;
    }

    public ArrayList<Comment> getCommentaires() {
        return commentaires;
    }

    public ArrayList<Attachment> getPjs() {
        return pjs;
    }

    /**
     * Définition de l'affichage d'un devoir (UI)
     * Quels widgets sont nécessaires pour l'affichage ?
     * Comment sont affichées les données ? etc
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.object_work, parent, false));
            name = (TextView) itemView.findViewById(R.id.card_title);
        }

        public void setWork(Work w) {
            // textview.setText() et compagnie
            name.setText(w.auteur);
        }

    }
}