package fr.agendapp.app.objects;

/**
 * Commentaire
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Comment {

    /** ID du commentaire dans la base */
    private int id;
    /** ID de l'auteur */
    private int user;
    /** "@prenomnom" de l'auteur */
    private String auteur;
    /** texte du commentaire */
    private String texte;
    /** Pièces jointes attachées au commentaire */
    private Attachment[] pjs;

    public Comment() {
        // TODO
    }

    /**
     * Supprimer le commentaire
     * - Supprimer localement du fil de commentaire
     * - Appel à l'API
     * Nécessite que l'utilisateur actuel en soit l'auteur
     * @return true si réussite
     */
    public boolean delete() {
        if (User.getInstance().getId() == this.user) {
            // TODO
            return false;
        } else {
            // TODO ajout d'un message (Toast ?)
            return false;
        }
    }

    public int getId() {
        return id;
    }

    public int getUser() {
        return user;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getText() {
        return texte;
    }

    public Attachment[] getPjs() {
        return pjs;
    }
}