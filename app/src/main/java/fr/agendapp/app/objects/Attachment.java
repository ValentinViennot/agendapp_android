package fr.agendapp.app.objects;

/**
 * Pièce jointe attachée à un commentaire ou à un devoir
 * @author Dylan Habans
 * @author Valentin Viennot
 */
class Attachment {

    /** "@prenomnom" de l'auteur de la pièce jointe */
    String author;
    /** ID de l'auteur */
    int user;
    /** Nom du fichier sur le serveur */
    String file;
    /** Nom lisible du fichier */
    String title;

    Attachment() {
        // TODO
    }

    /**
     * Lance le téléchargement de la pièce jointe sur l'appareil de l'utilisateur
     */
    void download () {
        // TODO
    }

    /**
     * Supprime la pièce jointe de la base de données
     * @return true si la pièce jointe est bien supprimée par l'utilisateur, false sinon
     */
    boolean delete (){
        // TODO
        // Attention : penser à vérifier que l'utilisateur actuel est bien l'auteur
        return false;
    }
}