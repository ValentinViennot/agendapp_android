package fr.agendapp.app.factories;

abstract class Pending {

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
        // TODO
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
        json+="]";
        return json;
    }


}