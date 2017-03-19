package fr.agendapp.app.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.ParseFactory;

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

    public Work() {
    }

    public static void setComingwork(Context context, String json, String version) {
        comingwork = ParseFactory.parseWork(json);
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("devoirs", json);
        editor.putString("versionD", version);
        editor.apply();
    }

    public static void setPastwork(Context context, String json, String version) {
        pastwork = ParseFactory.parseWork(json);
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("archives", json);
        editor.putString("versionA", version);
        editor.apply();
    }

    public static List<Work> getPastwork(Context activity) {
        if (pastwork == null) {
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

    /**
     * Marque comme fait/non fait selon le statut actuel
     * @return true si le devoir est marqué comme fait par l'utilisateur, false sinon
     */
    boolean done() {
        // TODO
        return false;
    }

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

    /**
     * @param c Commentaire à ajouter au devoir
     * @return true en cas de succes
     */
    boolean addComment(Comment c) {
        // TODO
        return false;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return auteur;
    }

    public Date getDate() {
        return date;
    }

    public int getUser() {
        return user;
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

    /**
     * Définition de l'affichage d'un devoir (UI)
     * Quels widgets sont nécessaires pour l'affichage ?
     * Comment sont affichées les données ? etc
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        LayoutInflater inflater;

        private CardView card;
        private RelativeLayout cardHeader;
        private TextView subject;
        private TextView text;
        private ImageButton flag;
        private TextView nbDone;
        private TextView nbComm;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.object_work, parent, false));
            this.inflater = inflater;
            card = (CardView) itemView.findViewById(R.id.card_view);
            cardHeader = (RelativeLayout) itemView.findViewById(R.id.card_header);
            subject = (TextView) itemView.findViewById(R.id.card_subject);
            text = (TextView) itemView.findViewById(R.id.card_text);
            flag = (ImageButton) itemView.findViewById(R.id.card_flag);
            nbDone = (TextView) itemView.findViewById(R.id.card_nbDone);
            nbComm = (TextView) itemView.findViewById(R.id.card_nbComment);
        }

        public void setWork(Work w) {
            // Matière
            subject.setText(w.getSubject());
//            subject.setTextColor(w.getSubjectColor());
            cardHeader.setBackgroundColor(w.getSubjectColor());
            // Texte du devoir
            text.setText(w.getText());
            // Drapeau / Marqueur
            int color;
            switch (w.getFlag()) {
                case 1:
                    color = Color.parseColor("#4178BE");
                    break;
                case 2:
                    color = Color.parseColor("#FF7832");
                    break;
                case 3:
                    color = Color.parseColor("#E71D32");
                    break;
                default:
                    color = Color.parseColor("#999999");
            }
            flag.setColorFilter(color);
            // Pièces jointes
            GridView gridview = (GridView) itemView.findViewById(R.id.card_attachments);
            gridview.setAdapter(new Attachment.AttachmentAdapter(w.getAttachments(), inflater));
            // Footer
            String nb = "" + w.getNbDone();
            nbDone.setText(nb);
            nb = "" + w.getComments().size();
            nbComm.setText(nb);
        }
    }
}