package fr.agendapp.app.factories;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import fr.agendapp.app.App;
import fr.agendapp.app.objects.Work;

/**
 * @author Dylan Habans
 */
class PendADD extends Pending {

    private static List<PendADD> pending;
    private static String name = "pendADD";

    // TODO vérifier que les champs correspondent à leur nom JSON (sur les autres pending)
    private String date;
    private String texte;
    private int user;

    /**
     * @param date Date d'échéance
     * @param text Description
     * @param groupe Matière (ID)
     * Constructeur de PendADD :
     */
    public PendADD(Date date, String text, int groupe) {
        Work.dateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        // Décalage d'horaire +2H forcé
        this.date = Work.dateformat.format(date) + "+02:00";
        this.texte = text;
        // l'API est configurée ainsi...
        this.user = groupe;
        pending.add(this);
    }

    /**
     * @return représentation JSON de la liste d'actions PendADD
     */
    static String getList() {
        ListIterator<PendADD> i = pending.listIterator();
        String json = "[";
        while (i.hasNext()) {
            json += i.next();
            if (i.hasNext()) json += ",";
        }
        json += "]";
        return json;
    }

    static void initList(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, Context.MODE_PRIVATE);
        pending = ParseFactory.parsePendADD(preferences.getString(name, "[]"));
    }

    static void saveList(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, getList());
        editor.apply();
    }

    static void clearList(Context context) {
        pending = new LinkedList<>();
        saveList(context);
    }

    static int size() {
        return pending.size();
    }

    public static String getName() {
        return name;
    }

    /**
     * @return Représentation JSON de l'action PendADD
     */
    public String toString() {
        String json = "{";
        json += "\"date\":\"" + date + "\",";
        json += "\"texte\":\"" + texte + "\",";
        json += "\"user\":" + user;
        json += "}";
        return json;
    }

}


