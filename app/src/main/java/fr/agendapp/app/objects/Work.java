package fr.agendapp.app.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.agendapp.app.App;
import fr.agendapp.app.factories.ParseFactory;
import fr.agendapp.app.factories.PendDO;

import static android.content.Context.MODE_PRIVATE;

/**
 * Représente un devoir
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Work {

    public static final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    private static List<Work> comingwork;
    private static List<Work> pastwork;
    /** ID dans la base */
    private int id;
    /** ID de l'auteur */
    private int user;
    /** "@prenomnom" de l'auteur */
    private String auteur;
    /** Nom de la matière */
    private String matiere;
    /** Couleur associée la matière */
    private String matiere_c;
    /** Texte du devoir */
    private String texte;
    /** Date d'échéance */
    private Date date;
    /** Nombre de marqué comme faits */
    private int nb_fait;
    /** Utilisateur a marqué comme fait ? */
    private int fait;
    /** Drapeau attaché par l'utilisateur */
    private int flag;
    /** Liste de commentaires */
    private ArrayList<Comment> commentaires;
    /** Liste de pièces jointes */
    private ArrayList<Attachment> pjs;

    /**
     * Constructeur par défaut (utilisé par Gson)
     */
    public Work() {
    }

    // STATIC RESOURCES

    /**
     * Devoirs à venir
     *
     * @param context Android Context
     * @param json    Représentation JSON de la liste de devoirs
     * @param version Chaine de version
     */
    public static void setComingwork(Context context, String json, String version) {
        comingwork = ParseFactory.parseWork(json);
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("devoirs", json);
        editor.putString("versionD", version);
        editor.apply();
    }

    /**
     * Devoirs passés (archives)
     *
     * @param context Android Context
     * @param json    Représentation JSON de la liste de devoirs
     * @param version Chaine de version
     */
    public static void setPastwork(Context context, String json, String version) {
        pastwork = ParseFactory.parseWork(json);
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("archives", json);
        editor.putString("versionA", version);
        editor.apply();
    }

    public static List<Work> getPastwork(Context activity) {
        // Si la liste n'est pas définie
        if (pastwork == null) {
            // On la récupére dans le stockage local de l'appareil
            SharedPreferences preferences = activity.getSharedPreferences(App.TAG, MODE_PRIVATE);
            pastwork = ParseFactory.parseWork(preferences.getString("archives", "[]"));
        }
        return pastwork;
    }

    public static List<Work> getComingwork(Context activity) {
        if (comingwork == null) {
            SharedPreferences preferences = activity.getSharedPreferences(App.TAG, MODE_PRIVATE);
            comingwork = ParseFactory.parseWork(preferences.getString("devoirs", "[]"));
        }
        return comingwork;
    }

    // SETTERS

    /**
     * Marque comme fait/non fait selon le statut actuel
     * TODO Déclencher update immédiat (ni de l'affichage ni de synchro)
     */
    public void done(Context context) {
        // Inverse la valeur (si était 0, devient 1-0 : 1 ; si était 1, devient 1-1 : 0)
        this.fait = 1 - this.fait;
        if (this.isDone()) {
            // le devoir vient d'être marqué comme fait
            // On augmente le nombre de marqué comme fait de 1
            this.nb_fait++;
        } else {
            // le devoir vient d'être marqué comme non fait
            // On diminue le nombre de marqué comme fait de 1
            this.nb_fait--;
        }
        // On ajoute l'action à la liste d'actions en attente
        new PendDO(context, this);
        // DEBUG
        Log.i(App.TAG, "ID " + this.getId() + " is " + this.isDone());
    }

    /**
     * Supprime le devoir
     * L'utilisateur doit en être le propriétaire
     * @return true si le devoir est supprimé par l'utilisateur, false sinon
     */
    public boolean delete() {
        // TODO
        return false;
    }

    /**
     * Signale le devoir au modérateur
     * L'utilisateur ne peut pas en être le propriétaire
     * @return true si le devoir est signalé par l'utilisateur, false sinon
     */
    public boolean report() {
        //TODO
        return false;
    }

    /**
     * @param c Commentaire à ajouter au devoir
     * @return true en cas de succes
     */
    public boolean addComment(Comment c) {
        // TODO
        return false;
    }

    // GETTERS

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return auteur;
    }

    public int getUser() {
        return user;
    }

    /**
     * @return true si l'utilisateur connecté est l'auteur du devoir
     */
    public boolean isUser() {
        return user == User.getInstance().getId();
    }

    public String getSubject() {
        return matiere;
    }

    public int getSubjectColor() {
        if (matiere_c == null) matiere_c = "000000";
        return Color.parseColor("#" + matiere_c);
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
        return fait > 0;
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


}