package fr.agendapp.app.pending;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import fr.agendapp.app.App;
import fr.agendapp.app.factories.ParseFactory;

/**
 * @author Dylan Habans
 */
public class PendCOMM extends Pending {

    private static List<PendCOMM> pending;
    private static String name = "pendCOMM";

    private int id;
    private String comment;

    /**
     * @param id      ID
     * @param comment Commentaire
     *                Constructeur de PendCOMM
     */
    public PendCOMM(Context context, int id, String comment) {
        this.id = id;
        this.comment = comment;
        pending.add(this);
        PendCOMM.saveList(context);
    }

    /**
     * @return représentation JSON de la liste d'actions PendCOMM
     */
    static String getList() {
        ListIterator<PendCOMM> i = pending.listIterator();
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
        pending = ParseFactory.parsePendCOMM(preferences.getString(name, "[]"));
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
     * @return représentation JSON de l'action PendCOMM
     */
    public String toString() {
        String json = "{";
        json += "\"id\":" + id + ",";
        json += "\"content\": {" +
                "\"texte\": \"" + comment + "\"";
        json += "}";
        json += "}";
        return json;
    }
}
