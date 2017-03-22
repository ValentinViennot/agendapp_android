package fr.agendapp.app.factories;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import fr.agendapp.app.App;

/**
 * @author Dylan Habans
 */
public class PendDELc extends Pending {

    private static final String name = "pendDELc";
    private static List<PendDELc> pending;
    private int id;

    public PendDELc(int id) {
        this.id = id;
        pending.add(this);
    }

    /**
     * @return représentation JSON de la liste d'actions PendDELc
     */
    static String getList() {
        ListIterator<PendDELc> i = pending.listIterator();
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
        pending = ParseFactory.parsePendDELc(preferences.getString(name, "[]"));
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
     * @return représentation JSON de l'action PendDELc
     */
    public String toString() {
        return ("" + id);
    }



}
