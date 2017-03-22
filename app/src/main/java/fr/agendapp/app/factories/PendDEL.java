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
public class PendDEL extends Pending {

    private final static String name = "pendDEL";
    private static List<PendDEL> pending;
    private int id;

    public PendDEL(int id) {
        this.id = id;
        pending.add(this);
    }

    /**
     * @return représentation JSON de la liste d'actions PendDEL
     */
    static String getList() {
        ListIterator<PendDEL> i = pending.listIterator();
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
        pending = ParseFactory.parsePendDEL(preferences.getString(name, "[]"));
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
     * @return représentation JSON de l'action PendDEL
     */
    public String toString() {
        return ("" + id);
    }
}
