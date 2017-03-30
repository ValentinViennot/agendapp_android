package fr.agendapp.app.factories;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import fr.agendapp.app.App;
import fr.agendapp.app.objects.Work;

/**
 * @author Dylan Habans
 */
public class PendDO extends Pending {

    private static String name = "pendDO";
    private static List<PendDO> pending;

    // TODO penser à vérifier que les noms des attributs sont bien les mêmes que ceux en JSON (sinon changer les attributs, pas le JSON) (pour les autres pending aussi)
    private int id;
    private boolean done;

    /**
     * @param id
     * @param done
     * Constructeur de PendDO
     */
    public PendDO(Context context, int id, boolean done) {
        this.id = id;
        this.done = done;
        pending.add(this);
        // TODO faire pareil pour les autres Pending (nécessite d'ajouter un paramètre Context)
        PendDO.saveList(context);
    }

    // TODO ajouter un constructeur avec l'objet concerné (pour les autres pending aussi)
    public PendDO(Context context, Work w) {
        this(context, w.getId(), w.isDone());
    }

    /**
     * @return représentation JSON de la liste d'actions PendDO
     */
    static String getList() {
        ListIterator<PendDO> i = pending.listIterator();
        String json = "[";
        while (i.hasNext()) {
            json += i.next();
            if (i.hasNext()) json += ",";
        }
        json+="]";
        return json;
    }

    static void initList(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, Context.MODE_PRIVATE);
        pending = ParseFactory.parsePendDO(preferences.getString(name, "[]"));
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
     * @return Représentation JSON de l'action PendDO
     */
    public String toString() {
        String json = "{";
        json += "\"id\":" + id + ",";
        json += "\"done\":" + done;
        json += "}";
        return json;
    }
}