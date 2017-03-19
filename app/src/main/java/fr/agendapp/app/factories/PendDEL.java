package fr.agendapp.app.factories;

import java.util.List;
import java.util.ListIterator;

public class PendDEL extends Pending {

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

    /**
     * @return représentation JSON de l'action PendDEL
     */
    public String toString() {
        String json = "";
        json += id;
        json += ",";
        return json;
    }
}
