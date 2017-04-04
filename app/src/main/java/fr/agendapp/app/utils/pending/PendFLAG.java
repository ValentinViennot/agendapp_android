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
public class PendFLAG extends Pending {

    private static List<PendFLAG> pending;
    private static String name = "pendFLAG";

    private int id;
    private int flag;

    /**
     * @param id Index du devoir auquel appliquer <code>flag</code>
     * @param flag index du marqueur à appliquer
     * Constructeur de PendFLAG
     */
    private PendFLAG(Context context, int id, int flag) {
        this.id = id;
        this.flag = flag;
        pending.add(this);
        PendFLAG.saveList(context);
    }

    public PendFLAG(Context context, Work w) {
        this(context, w.getId(), w.getFlag());
    }

    /**
     * @return représentation JSON de la liste d'actions PendFLAG
     */
    static String getList() {
        ListIterator<PendFLAG> i = pending.listIterator();
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
        pending = ParseFactory.parsePendFLAG(preferences.getString(name, "[]"));
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
     * @return représentation JSON de l'action PendFLAG
     */
    public String toString() {
        String json = "{";
        json += "\"id\":" + id + ",";
        json += "\"flag\":" + flag;
        json += "}";
        return json;
    }
}
