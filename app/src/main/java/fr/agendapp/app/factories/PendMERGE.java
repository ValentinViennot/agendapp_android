package fr.agendapp.app.factories;


import android.content.Context;
import android.content.SharedPreferences;
import fr.agendapp.app.App;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Dylan Habans
 */
public class PendMERGE extends Pending {

    private static List<PendMERGE> pending;
    private static String name = "pendMERGE";

    private int[] ids;

    public PendMERGE(int[] ids) {
        this.ids = ids;
        pending.add(this);
    }

    /**
     * @return représentation JSON de la liste d'actions PendDELc
     */
    static String getList() {
        ListIterator<PendMERGE> i = pending.listIterator();
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
        pending = ParseFactory.parsePendMERGE(preferences.getString(name, "[]"));
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

    public String toString() {
        String json = "[";
        for (int i = 0; i < ids.length; i++) {
            json += ids[i];
            json += ",";
        }
        json = json.substring(0, json.length() - 1); // supprime la dernière virgule
        json += "]";
        return json;
    }
}
