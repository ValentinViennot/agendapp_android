package fr.agendapp.app.factories;


import java.util.List;
import java.util.ListIterator;

public class PendDELc extends Pending {

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

    /**
     * @return représentation JSON de l'action PendDELc
     */
    public String toString() {
        String json = "";
        json += id;
        json += ",";
        return json;
    }



}
