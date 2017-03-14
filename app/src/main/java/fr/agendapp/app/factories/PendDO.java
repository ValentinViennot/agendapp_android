package fr.agendapp.app.factories;

import java.util.List;
import java.util.ListIterator;

class PendDO extends Pending {

    private static List<PendDO> pending;

    private int id;
    private boolean done;

    /**
     * @param id
     * @param done
     * @author Valentin Viennot
     * Constructeur de PendDO
     */
    public PendDO(int id, boolean done) {
        this.id = id;
        this.done = done;
        pending.add(this);
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

    /**
     * @return Représentation JSON de l'action PendDO
     */
    public String toString() {
        String json = "{";
        json += "\"id\":" + id + ",";
        json += "\"done\":" + (done ? "true" : "false");
        json += "}";
        return json;
    }
}