package fr.agendapp.app.utils.pending;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import fr.agendapp.app.App;
import fr.agendapp.app.factories.ParseFactory;
import fr.agendapp.app.objects.Work;

/**
 * @author Dylan Habans
 */
public class PendALERT extends Pending {

    private static List<PendALERT> pending;
    private static String name = "pendALERT";

    private int id;

    public PendALERT(Context context, int id) {
        this.id = id;
        pending.add(this);
        PendALERT.saveList(context);
    }

    public PendALERT(Context context, Work w) {
        this(context, w.getId());
    }

    /**
     * @return représentation JSON de la liste d'actions PendALERT
     */
    static String getList() {
        ListIterator<PendALERT> i = pending.listIterator();
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
        pending = ParseFactory.parsePendALERT(preferences.getString(name, "[]"));
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
     * @return représentation JSON de l'action PendALERT
     */
    public String toString() {
        return "" + id;
    }


}
