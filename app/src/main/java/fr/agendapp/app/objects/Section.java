package fr.agendapp.app.objects;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.R;

/**
 * @author Dylan Habans
 * @author Valentin Viennot
 * @author Lucas Probst
 * @author Charline Bardin
 */
public class Section {

    /**
     * Nom du mois
     */
    private String month;
    /**
     * Nom du jour de la semaine
     */
    private String day;
    /**
     * Numéro du jour du mois
     */
    private int date;
    /**
     * Devoirs correspondant à cette section (date)
     */
    private List<Work> homeworks;


    private Section(int date, String month, String day) {
        this.date = date;
        this.month = month;
        this.day = day;
        this.homeworks = new LinkedList<>();
    }

    /**
     * @param context Android
     * @param w       Devoir modèle de la section
     * @param b       Afficher le mois ?
     * @return Section instanciée
     */
    private static Section getSection(Context context, Work w, boolean b) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(w.getDate());
        return new Section(
                cal.get(Calendar.DAY_OF_MONTH),
                b ? getMonthName(context, cal.get(Calendar.MONTH)) : null,
                getWeekName(context, cal.get(Calendar.DAY_OF_WEEK))
        );
    }

    /**
     * Transforme une liste de devoirs (déjà triée) en une liste de sections
     * Une liste de section ne doit pas changer l'ordre des devoirs
     * Une liste de section doit séparer les devoirs par date en faisant apparaitre le mois si différent du précédent (sinon , vaut null)
     * @return Liste de sections transformée
     */
    static List<Section> getSections(Context context, List<Work> liste) {
        // Liste de Sections retournée
        List<Section> result = new ArrayList<>();
        if (liste.size() == 0) return result;
        // Récupére une instance de Calendrier
        // Pour la date de la section en cours
        Calendar cal = Calendar.getInstance();
        // Et une instance pour comparer
        Calendar cal2 = Calendar.getInstance();
        // Initialise la première section avec les paramètres du premier devoir
        Section s = getSection(context, liste.get(0), true);
        cal.setTime(liste.get(0).getDate());
        // --- Remarque ---
        // Cette écriture de la boucle "for" revient à utiliser un Iterateur
        // ListIterator<Work> i = liste.listiterator();
        // while(i.hasNext()) function(i.next());
        // --> Nettement plus performant qu'un for(int i=0;i<liste.size();++i) etc.
        // --- ---  --- ---
        for (Work w : liste) {
            // Date du devoir traité
            cal2.setTime(w.getDate());
            // Si le mois et/ou le jour sont différents
            if (
                    cal.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)
                            || cal.get(Calendar.DAY_OF_MONTH) != cal2.get(Calendar.DAY_OF_MONTH)
                    ) {
                // On passe à une nouvelle section
                // Ajoute la précédente au résultat
                result.add(s);
                // Récupère une nouvelle Section
                s = getSection(context, w, cal.get(Calendar.MONTH) != cal2.get(Calendar.MONTH));
                // Met à jour la date de la section en cours
                cal.setTime(w.getDate());
            }
            // Ajoute le devoir à la section en cours
            s.add(w);
        }
        // Ajoute la dernière Section au résultat
        result.add(s);
        return result;
    }

    /**
     * Remarque : L'utilisation des ressources permettra de traduire l'application en plusieurs langues
     *
     * @param month   Numéro du mois
     * @param context Android Context (Accès aux ressources)
     * @return nom du mois correspondant à la date
     */
    private static String getMonthName(Context context, int month) {
        Resources r = context.getResources();
        switch (month) {
            case 0:
                return r.getString(R.string.janvier);
            case 1:
                return r.getString(R.string.fevrier);
            case 2:
                return r.getString(R.string.mars);
            case 3:
                return r.getString(R.string.avril);
            case 4:
                return r.getString(R.string.mai);
            case 5:
                return r.getString(R.string.juin);
            case 6:
                return r.getString(R.string.juillet);
            case 7:
                return r.getString(R.string.aout);
            case 8:
                return r.getString(R.string.septembre);
            case 9:
                return r.getString(R.string.octobre);
            case 10:
                return r.getString(R.string.novembre);
            case 11:
                return r.getString(R.string.decembre);
        }
        return null;
    }

    /**
     * @param context Android
     * @param day     Jour de la semaine
     * @return représentation locale du jour de la semaine
     */
    private static String getWeekName(Context context, int day) {
        Resources r = context.getResources();
        switch (day) {
            case 0:
                return r.getString(R.string.dimanche);
            case 1:
                return r.getString(R.string.lundi);
            case 2:
                return r.getString(R.string.mardi);
            case 3:
                return r.getString(R.string.mercredi);
            case 4:
                return r.getString(R.string.jeudi);
            case 5:
                return r.getString(R.string.vendredi);
            case 6:
                return r.getString(R.string.samedi);
        }
        return null;
    }

    // GETTERS

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public int getDate() {
        return date;
    }

    public List<Work> getHomeworks() {
        return homeworks;
    }

    public void add(Work w) {
        this.homeworks.add(w);
    }
}