/**
 * Invitation à rejoindre un groupe
 * @author Dylan Habans
 * @author Valentin Viennot
 */
class Invite {

    /** ID dans la base */
    int id;
    /** Prénom de l'utilisateur qui invite */
    String from;
    /** Nom du groupe */
    String group;
    /** ID du groupe invité */
    int group_id;

    Invite() {

    }

    /**
     * Accepte l'invitation (donc rejoins le groupe , API)
     * @return true si réussite
     */
    boolean accept() {

    }

    /**
     * Refuse l'invitation (supprime)
     * @return true si réussite
     */
    void decline() {

    }
}