package fr.agendapp.app.objects;

/**
 * Pièce jointe attachée à un commentaire ou à un devoir
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Attachment {

    /** "@prenomnom" de l'auteur de la pièce jointe */
    private String auteur;
    /** ID de l'auteur */
    private int user;
    /** Nom du fichier sur le serveur */
    private String file;
    /** Nom lisible du fichier */
    private String title;

    public Attachment() {
        // TODO
    }

    /**
     * Lance le téléchargement de la pièce jointe sur l'appareil de l'utilisateur
     */
    public void download() {
        // TODO
    }

    /**
     * Supprime la pièce jointe de la base de données
     * @return true si la pièce jointe est bien supprimée par l'utilisateur, false sinon
     */
    public boolean delete() {
        // TODO
        // Attention : penser à vérifier que l'utilisateur actuel est bien l'auteur
        return false;
    }

    // GETTERS

    public String getAuteur() {
        return auteur;
    }

    public int getUser() {
        return user;
    }

    public String getFile() {
        return file;
    }

    public String getTitle() {
        return title;
    }

    /**
     * @param token Token d'identification aux APIs
     * @return URL (lien) d'accès au fichier
     */
    public String getLink(String token) {
        return (
                "https://apis.agendapp.fr/cdn/?get=" + this.file + "&token=" + token
        );
    }
}