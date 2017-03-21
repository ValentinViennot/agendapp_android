package fr.agendapp.app.factories;

import android.content.Context;
import android.util.Log;

import fr.agendapp.app.App;
import fr.agendapp.app.pages.SyncListener;

public abstract class Pending {

    // private static List<Pending> pending;

    /**
     * Récupérer l'ancienne pending list du stockage local
     * (à appeler à l'ouverture)
     */
    static void init(Context context) {
        // TODO
        PendDO.initList(context);
        PendFLAG.initList(context);
    }

    /**
     * Enregistre la pending list dans le stockage local
     * (à appeler à la fermeture ou après chaque send)
     */
    static void save(Context context) {
        // TODO
        PendDO.saveList(context);
        PendFLAG.save(context);
    }

    /**
     * Envoie la pendingList au serveur pour traitement
     */
    public static void send(SyncListener syncListener, Context context) {
        Log.i(App.TAG, "test : Pending.send ");
        // Récupération des actions en attente au format JSON
        String json = toJson();
        // S'il y a des actions en attente
        if (json != null) {
            // On les sauvegarde dans l'attente de l'envoi
            save(context);
            // On envoi les actions en attente (suivi d'une récupération des devoirs au SyncListener)
            SyncFactory.getInstance(context).synchronize(syncListener, context, json);
        } else {
            // Lorsqu'il n'y a pas d'actions en attente
            // On se contente de demander la nouvelle version des devoirs
            SyncFactory.getInstance(context).getVersion(syncListener, context);
        }
    }

    static void clear(Context context) {
        // TODO
        PendDO.clearList(context);
        PendFLAG.clearList(context);
    }


    public static String toJson() {
        // TODO
        boolean first = true;
        int size = 0;
        String json = "[";
        if (PendDO.size() > 0) {
            size += PendDO.size();
            json += "\"" + PendDO.getName() + "\":" + PendDO.getList();
            first = false;
        }
        if (PendFLAG.size() > 0) {
            size += PendFLAG.size();
            if (!first) json += ",";
            json += "\"" + PendFLAG.getName() + "\":" + PendFLAG.getList();
            first = false;
        }
        // TODO etc.
//        json += "\"pendADD\":" + PendADD.getList();
//        json += ",";
//        json += "\"pendALERT\":" + PendALERT.getList();
//        json += ",";
//        json += "\"pendCOMM\":" + PendCOMM.getList();
//        json += ",";
//        json += "\"pendDELc\":" + PendDELc.getList();
//        json += ",";
//        json += "\"pendMERGE\":" + PendMERGE.getList();
//        json += ",";
//        json += "\"pendDEL\":" + PendDEL.getList();
        json+="]";
        return size > 0 ? json : null;
    }

}