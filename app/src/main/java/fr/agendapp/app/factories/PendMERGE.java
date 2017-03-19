package fr.agendapp.app.factories;


import java.util.List;
import java.util.ListIterator;

public class PendMERGE extends Pending {

    private static List<PendMERGE> pending;
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

    public String toString() {
        String json = "";
        for (PendMERGE p : pending) {
            json += ids;
            json += ",";
        }
        return json;

    }
}
