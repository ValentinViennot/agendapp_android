package fr.agendapp.app.listeners;

/**
 * Listener pour la synchronisation
 */
public interface SyncListener {

    /**
     * Méthode à exécuter en cas de réussite de la synchronisation :
     * De nouvelles données sont arrivées (et disponibles dans le stockage local)
     */
    void onSync();

    /**
     * Méthode à exécuter en cas de synchronisation non effective : pas de nouvelles données
     */
    void onSyncNotAvailable();

    /**
     * @return True si les devoirs à traiter sont des archives et false si ce sont les devoirs à venir
     */
    boolean isArchives();
}
