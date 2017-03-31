package fr.agendapp.app.factories;

import android.content.Context;
import android.content.SharedPreferences;
import fr.agendapp.app.App;
import fr.agendapp.app.objects.Work;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Dylan Habans
 */
public class PendDEL extends Pending {

    private final static String name = "pendDEL";
    private static List<PendDEL> pending;
    private int id;

    public PendDEL(Context context, int id) {
        this.id = id;
        pending.add(this);
        PendDEL.saveList(context);
    }

    public PendDEL(Context context, Work w) {
        this(context, w.getId());
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
