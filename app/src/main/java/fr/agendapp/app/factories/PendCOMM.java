package fr.agendapp.app.factories;

import java.util.List;
import java.util.ListIterator;

public class PendCOMM extends Pending {

    private static List<PendCOMM> pending;
    private int id;
    private String comment;

    /**
     * @param id
     * @param comment
     * @author Dylan Habans
     * Constructeur de PendCOMM
     */
    public PendCOMM(int id, String comment) {
        this.id = id;
        this.comment = comment;
        pending.add(this);
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

    /**
     * @return représentation JSON de l'action PendCOMM
     */
    public String toString() {

        String json = "{";
        json += "\"id\":" + id + ",";
        json += "\"content\": {" +
                "\"texte\": \"" + comment + "\"";
        json += "}";

        return json;
    }
}
