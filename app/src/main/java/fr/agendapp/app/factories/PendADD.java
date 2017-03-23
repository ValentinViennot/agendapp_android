package fr.agendapp.app.factories;

import android.content.Context;
import android.content.SharedPreferences;
import fr.agendapp.app.App;
import fr.agendapp.app.objects.Work;

import java.util.*;

class PendADD extends Pending {

    private static List<PendADD> pending;
    private static String name = "pendADD";

    private String date;
    private String text;
    private int groupe;

    /**
     * @param date
     * @param text
     * @param groupe
     * @author Dylan Habans
     * Constructeur de PendADD :
     */
    public PendADD(Date date, String text, int groupe) {
        Work.dateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.date = Work.dateformat.format(date) + "+02:00";
        this.text = text;
        this.groupe = groupe;
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
        json += "\"texte\":\"" + text + "\",";
        json += "\"user\":" + groupe;
        json += "}";
        return json;
    }

}


