package fr.agendapp.app.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import fr.agendapp.app.App;
import fr.agendapp.app.factories.ParseFactory;
import fr.agendapp.app.factories.PendALERT;
import fr.agendapp.app.factories.PendCOMM;
import fr.agendapp.app.factories.PendDEL;
import fr.agendapp.app.factories.PendDO;
import fr.agendapp.app.factories.PendFLAG;

import static android.content.Context.MODE_PRIVATE;

/**
 * Représente un devoir
 *
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Work {

    public static final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    private static List<Work> comingwork;
    private static List<Work> pastwork;
    /**
     * ID dans la base
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
     * Nom de la matière
     */
    private String matiere;
    /**
     * Couleur associée la matière
     */
    private String matiere_c;
    /**
     * Texte du devoir
     */
    private String texte;
    /**
     * Date d'échéance
     */
    private Date date;
    /**
     * Nombre de marqué comme faits
     */
    private int nb_fait;
    /**
     * Utilisateur a marqué comme fait ?
     */
    private int fait;
    /**
     * Drapeau attaché par l'utilisateur
     */
    private int flag;
    /**
     * Liste de commentaires
     */
    private LinkedList<Comment> commentaires;
    /**
     * Liste de pièces jointes
     */
    private ArrayList<Attachment> pjs;

    /**
     * Constructeur par défaut (utilisé par Gson)
     */
    public Work() {
    }

    /**
     * Contruction d'un devoir et ajout à la liste ComingWork dans l'ordre chronologique
     *
     * @param user    Auteur
     * @param subject Matière
     * @param texte   Texte
     * @param date    Date d'échéance
     */
    public Work(User user, Subject subject, String texte, Date date) {
        this.id = 0;
        // Tout est normal...
        this.user = subject.getId();
        this.auteur = user.getPrenom() + user.getNom();
        this.texte = texte;
        this.date = date;
        this.matiere = subject.getNom();
        this.matiere_c = subject.getHex();
        this.nb_fait = 0;
        this.fait = 0;
        this.flag = 0;
        this.commentaires = new LinkedList<>();
        this.pjs = new ArrayList<>();
        insert(this);
    }

    private static void insert(Work w) {
        ListIterator<Work> iterator = comingwork.listIterator();
        while (iterator.hasNext())
            if (w.getDate().compareTo(iterator.next().getDate()) >= 0) {
                iterator.add(w);
                break;
            }
    }

    // STATIC RESOURCES

    // TODO sauvegarder tableau local ?

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
        if (activity != null && comingwork == null) {
            SharedPreferences preferences = activity.getSharedPreferences(App.TAG, MODE_PRIVATE);
            comingwork = ParseFactory.parseWork(preferences.getString("devoirs", "[]"));
        }
        return comingwork;
    }

    // SETTERS

    /**
     * Marque comme fait/non fait selon le statut actuel
     * TODO enregistrer les modifications au localStorage (implémenter sur les autres méthodes)
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
    }

    /**
     * Supprime le devoir
     * L'utilisateur doit en être le propriétaire
     */
    public void delete(Context context) {
        if (comingwork.contains(this)) {
            comingwork.remove(this);
        } else {
            pastwork.remove(this);
        }
        new PendDEL(context, this);
    }

    /**
     * Signale le devoir au modérateur
     * L'utilisateur ne peut pas en être le propriétaire
     */
    public void report(Context context) {
        new PendALERT(context, this);
    }

    /**
     * @param c Commentaire à ajouter au devoir
     */
    public void addComment(Context context, Comment c) {
        this.commentaires.add(c);
        new PendCOMM(context, c);
    }

    public void setFlag(Context context, int flag) {
        this.flag = flag;
        new PendFLAG(context, this);
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

    public LinkedList<Comment> getComments() {
        return commentaires;
    }

    public ArrayList<Attachment> getAttachments() {
        return pjs;
    }

    /**
     * On cinsidère un devoir comme ayant été modifié si un des paramètres variables a évoluer
     * La description, la matière, l'auteur... sont tant de paramètres non variables
     *
     * @param w Devoir à comparer avec this
     * @return true si différent
     */
    public boolean modified(Work w) {
        return (
                this.getId() != w.getId()
                        || this.getFlag() != w.getFlag()
                        || this.isDone() != w.isDone()
                        || this.getSubjectColor() != w.getSubjectColor()
                        || this.getNbDone() != w.getNbDone()
                        || this.getComments().size() != w.getComments().size()
                        || (this.getComments().size() != 0 && w.getComments().size() != 0 && this.getComments().getLast().getId() != w.getComments().getLast().getId())
        );
    }

}