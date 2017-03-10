package fr.agendapp.app.services;

abstract class Pending {

    static Pending[] pendingList = new Pending[8];

    /**
     * Récupérer l'ancienne pending list du stockage local
     * (à appeler à l'ouverture)
     */
    static void init() {
        // TODO
        // Charge depuis le stockage si existant
        // Sinon
        pendingList[1] = new PendDO();
    }

    /**
     * Enregistre la pending list dans le stockage local
     * (à appeler à la fermeture ou après chaque send)
     */
    static void save() {
        // TODO
        // Enregistre la pending List
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
        json+="\"pendDO\":"+pendingList[1];
        //json+=",";
        json+="]";
        return json;
    }

    abstract void add(Object o);

}