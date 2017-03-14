package fr.agendapp.app.factories;

import java.util.List;
import java.util.ListIterator;

class PendFLAG extends Pending {

    private static List<PendFLAG> pending;
    private int id;
    private int flag;

    /**
     * @param id
     * @param flag
     * @author Dylan Habans
     * Constructeur de PendFLAG
     */
    public PendFLAG(int id, int flag) {
        this.id = id;
        this.flag = flag;
        pending.add(this);
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
