package fr.agendapp.app.objects;

/**
 * Invitation à rejoindre un groupe
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Invite {

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
        return false;
    }

    /**
     * Refuse l'invitation (supprime)
     * @return true si réussite
     */
    void decline() {

    }
}