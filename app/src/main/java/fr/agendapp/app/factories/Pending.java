package fr.agendapp.app.factories;

abstract class Pending {

    // private static List<Pending> pending;

    /**
     * Récupérer l'ancienne pending list du stockage local
     * (à appeler à l'ouverture)
     */
    static void init() {
        // TODO
    }

    /**
     * Enregistre la pending list dans le stockage local
     * (à appeler à la fermeture ou après chaque send)
     */
    static void save() {
        // TODO
    }

    /**
     * Envoie la pendingList au serveur pour traitement
     */
    static void send() {
        // TODO
    }

    public static String toJson() {

        String json = "[";
        json += "\"pendADD\":" + PendADD.getList();
        json += ",";
        json += "\"pendDO\":" + PendDO.getList();
        json += ",";
        json += "\"pendFLAG\":" + PendFLAG.getList();
        json += ",";
        json += "\"pendALERT\":" + PendALERT.getList();
        json += ",";
        json += "\"pendCOMM\":" + PendCOMM.getList();
        json += ",";
        json += "\"pendDELc\":" + PendDELc.getList();
        json += ",";
        json += "\"pendMERGE\":" + PendMERGE.getList();
        json += ",";
        json += "\"pendDEL\":" + PendDEL.getList();
        json+="]";
        return json;
    }

    /** abstract static String getList(List<Pending>pending) {
     ListIterator<Pending> i = pending.listIterator();
     String json = "[";
     while (i.hasNext()) {
     json += i.next();
     if (i.hasNext()) json += ",";
     }
     json+="]";
     return json;
     }
     **/

}