package fr.agendapp.app.factories;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class PendADD extends Pending {

    private static List<PendADD> pending;

    private String date;
    private String text;
    private int groupe;

    /**
     * @param date
     * @param text
     * @param groupe
     * @author Dylan Habans
     * Constructeur de PendADD :
     */
    public PendADD(Date date, String text, int groupe) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.date = format.format(date) + "+02:00";
        this.text = text;
        this.groupe = groupe;
        pending.add(this);
    }

    /**
     * @return représentation JSON de la liste d'actions PendADD
     */
    static String getList() {
        ListIterator<PendADD> i = pending.listIterator();
        String json = "[";
        while (i.hasNext()) {
            json += i.next();
            if (i.hasNext()) json += ",";
        }
        json += "]";
        return json;
    }

    /**
     * @return Représentation JSON de l'action PendADD
     */
    public String toString() {
        String json = "{";
        json += "\"date\":\"" + date + "\",";
        json += "\"texte\":\"" + text + "\",";
        json += "\"user\":" + groupe;
        json += "}";
        return json;
    }

}


