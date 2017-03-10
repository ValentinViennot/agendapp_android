package fr.agendapp.app.objects;

/**
 * Commentaire
 * @author Dylan Habans
 * @author Valentin Viennot
 */
class Comment {

    /** ID du commentaire dans la base */
    int id;
    /** ID de l'auteur */
    int user;
    /** "@prenomnom" de l'auteur */
    String author;
    /** texte du commentaire */
    String text;
    /** Pièces jointes attachées au commentaire */
    Attachment[] PJ;

    Comment() {
        // TODO
    }

    /**
     * Supprimer le commentaire
     * @return true si réussite
     */
    boolean delete() {
        return false;
    }

}