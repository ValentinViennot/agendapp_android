package fr.agendapp.app.factories;

import java.util.List;
import java.util.ListIterator;

public class PendALERT extends Pending {

    private static List<PendALERT> pending;
    private int id;

    public PendALERT(int id) {
        this.id = id;
        pending.add(this);

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

    /**
     * @return représentation JSON de l'action PendALERT
     */
    public String toString() {
        return ("" + id);
    }


}
