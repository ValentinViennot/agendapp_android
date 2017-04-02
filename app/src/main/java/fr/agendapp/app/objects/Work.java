package fr.agendapp.app.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import fr.agendapp.app.App;
import fr.agendapp.app.factories.ParseFactory;
import fr.agendapp.app.pending.PendALERT;
import fr.agendapp.app.pending.PendCOMM;
import fr.agendapp.app.pending.PendDEL;
import fr.agendapp.app.pending.PendDO;
import fr.agendapp.app.pending.PendFLAG;

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
    private static List<Integer> changed_A = new LinkedList<>();
    private static List<Integer> added_A = new LinkedList<>();
    private static List<Integer> removed_A = new LinkedList<>();
    private static List<Integer[]> moved_A = new LinkedList<>();
    private static List<Integer> changed_D = new LinkedList<>();
    private static List<Integer> added_D = new LinkedList<>();
    private static List<Integer> removed_D = new LinkedList<>();
    private static List<Integer[]> moved_D = new LinkedList<>();
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

    // STATIC RESOURCES
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

    // SETTERS

    private static void insert(Work w) {
        int index = comingwork.size();
        for (Work a : comingwork)
            if (w.getDate().compareTo(a.getDate()) <= 0) {
                index = comingwork.indexOf(a);
                break;
            }
        comingwork.add(index, w);
    }

    public static void saveList(Context context, boolean b) {
        String json;
        if (b)
            json = ParseFactory.workToJson(pastwork);
        else
            json = ParseFactory.workToJson(comingwork);
        saveList(context, b, json, "0");
    }

    public static void saveList(Context context) {
        saveList(context, true);
        saveList(context, false);
    }

    public static void saveList(Context context, boolean b, String json, String version) {
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString((b ? "archives" : "devoirs"), json);
        editor.putString("version" + (b ? "A" : "D"), version);
        editor.apply();
    }

    /**
     * Devoirs à venir
     *
     * @param context Android Context
     * @param json    Représentation JSON de la liste de devoirs
     * @param version Chaine de version
     */
    public static void setComingwork(Context context, String json, String version) {
        comingwork = updateList(getComingwork(context), ParseFactory.parseWork(json), false);
        saveList(context, false, json, version);
    }

    /**
     * Devoirs passés (archives)
     *
     * @param context Android Context
     * @param json    Représentation JSON de la liste de devoirs
     * @param version Chaine de version
     */
    public static void setPastwork(Context context, String json, String version) {
        pastwork = updateList(getPastwork(context), ParseFactory.parseWork(json), true);
        saveList(context, true, json, version);
    }

    public static List<Work> getPastwork(Context activity) {
        // Si la liste n'est pas définie
        if (pastwork == null) {
            // On la récupére dans le stockage local de l'appareil
            SharedPreferences preferences = activity.getSharedPreferences(App.TAG, MODE_PRIVATE);
            pastwork = ParseFactory.parseWork(preferences.getString("archives", "[]"));
            for (int i = 0; i < pastwork.size(); i++)
                added_A.add(i);
        }
        return pastwork;
    }

    public static List<Work> getComingwork(Context activity) {
        if (activity != null && comingwork == null) {
            SharedPreferences preferences = activity.getSharedPreferences(App.TAG, MODE_PRIVATE);
            comingwork = ParseFactory.parseWork(preferences.getString("devoirs", "[]"));
            for (int i = 0; i < comingwork.size(); i++)
                added_D.add(i);
        }
        return comingwork;
    }

    /**
     * Observe les modifications entre deux listes, une "ancienne" et une "nouvelle"
     *
     * @param o        Ancienne liste de devoirs
     * @param n        Nouvelle liste de devoirs
     * @param archives True si archives, false si devoirs
     * @return Ancienne liste mise à jour à partir de la nouvelle
     */
    private static List<Work> updateList(List<Work> o, List<Work> n, boolean archives) {
        Log.i(App.TAG, "updateList");
        // Récupère le bon objet pour les listes de modifications
        List<Integer> changed = archives ? changed_A : changed_D;
        List<Integer> added = archives ? added_A : added_D;
        List<Integer> removed = archives ? removed_A : removed_D;
        List<Integer[]> moved = archives ? moved_A : moved_D;
        // Liste de retour
        List<Work> res = new LinkedList<>();
        int index;
        Work v;
        int count = 0;
        // Pour chaque element de la nouvelle liste
        for (Work w : n) {
            // S'il n'était pas dans l'ancienne
            if ((index = indexOf(w, o)) < 0) {
                // On ajoute la référence vers ce nouvel objet à la liste
                res.add(w);
                // On signale l'insertion d'un nouvel élément à cet endroit de la liste
                added.add(count);
            }
            // S'il y était déjà mais qu'il a été modifié
            else if ((v = o.get(index)).modified(w)) {
                // On y ajoute la référence vers l'ancien objet après avoir mis à jour ce dernier
                res.add(v.copyFrom(w));
                // On signale la modification d'un élément à cette position
                changed.add(count);
            }
            // Sinon, l'élément y était déjà et n'a pas été modifié
            else {
                // On se contente de replacer la référence à l'objet au nouvel endroit dans la liste
                res.add(v);
                // Si la nouvelle position n'est pas la même que l'ancienne, on signale un déplacement
                moved.add(new Integer[]{index, count});
            }
            count++;
        }
        // Pour chaque élément de l'ancienne liste
        count = 0;
        for (Work w : o) {
            // S'il n'est plus dans la nouvelle
            if (indexOf(w, n) < 0)
                // On signale sa suppression
                removed.add(count);
            count++;
        }
        // Transformation des removed+added en "changed"
        /*for (Integer i : added)
            for (Integer j : removed)
                if (i.equals(j)) {
                    added.remove(i);
                    removed.remove(j);
                    changed.add(i);
                }*/
        return res;
    }

    public static List<Integer> getChanged(boolean archives) {
        return archives ? changed_A : changed_D;
    }

    public static List<Integer> getAdded(boolean archives) {
        return archives ? added_A : added_D;
    }

    public static void notifyItemRemoved(int position, Work w) {
        if (pastwork.contains(w))
            removed_A.add(position);
        else
            removed_D.add(position);
    }

    public static List<Integer> getRemoved(boolean archives) {
        return archives ? removed_A : removed_D;
    }

    public static List<Integer[]> getMoved(boolean archives) {
        return archives ? moved_A:moved_D;
    }

    public static void setChangesApplied(boolean archives) {
        if (archives) {
            added_A.clear();
            moved_A.clear();
            changed_A.clear();
            removed_A.clear();
        } else {
            added_D.clear();
            moved_D.clear();
            changed_D.clear();
            removed_D.clear();
        }
    }

    /**
     * @param list Liste de devoirs (Work)
     * @param e    Element Work
     * @return Index du devoir dans la liste (recherche par ID) ou -1 si non présent
     */
    private static int indexOf(Work e, List<Work> list) {
        for (Work w : list)
            if (w.getId() == e.getId())
                return list.indexOf(w);
        return -1;
    }

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
        // Sauvegarde la liste de devoirs locale
        saveList(context);
    }

    /**
     * Supprime le devoir
     * L'utilisateur doit en être le propriétaire
     */
    public void delete(Context context) {
        // TODO réorganiser les listes pour qu'une seule liste de devoirs subsiste
        // La liste de l'adapter (filtrée + associée aux headers) doit être maj dès que celle locale a changée
        // Soit parce que celle locale a été mise à jour avec le serveur, soit parce qu'elle a été modifiée localement
        // TODO : attribut static haschanged ? (par exemple)
        if (comingwork.contains(this)) {
            comingwork.remove(this);
            // Sauvegarde la liste de devoirs locale
            saveList(context, false);
        } else {
            pastwork.remove(this);
            // Sauvegarde la liste de devoirs locale
            saveList(context, true);
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

    // GETTERS

    /**
     * @param c Commentaire à ajouter au devoir
     */
    public void addComment(Context context, Comment c) {
        this.commentaires.add(c);
        new PendCOMM(context, this.getId(), c.getText());
        // Sauvegarde la liste de devoirs locale
        saveList(context);
    }

    public void setFlag(Context context, int flag) {
        this.flag = flag;
        new PendFLAG(context, this);
        // Sauvegarde la liste de devoirs locale
        saveList(context);
    }

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
                        || this.getAttachments().size() != w.getAttachments().size()
                        || this.getComments().size() != w.getComments().size()
                        || (this.getComments().size() != 0 && w.getComments().size() != 0 && this.getComments().getLast().getId() != w.getComments().getLast().getId())
        );
    }

    private Work copyFrom(Work w) {
        this.flag = w.flag;
        this.fait = w.fait;
        this.nb_fait = w.nb_fait;
        this.matiere_c = w.matiere_c;
        this.commentaires = w.commentaires;
        this.pjs = w.pjs;
        return this;
    }

}