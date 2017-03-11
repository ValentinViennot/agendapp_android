package fr.agendapp.app.objects;

/**
 * Invitation à rejoindre un groupe
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Invite {

    /** ID dans la base */
    private int id;
    /** Prénom de l'utilisateur qui invite */
    private String de;
    /** Nom du groupe */
    private String groupe;
    /** ID du groupe invité */
    private int groupeid;

    public Invite() {

    }

    /**
     * Accepte l'invitation (donc rejoins le groupe , API)
     * @return true si réussite
     */
    public boolean accept() {
        // TODO
        return false;
    }

    /**
     * Refuse l'invitation (supprime)
     * @return true si réussite
     */
    public void decline() {
        // TODO
    }

    // GETTERS

    public int getId() {
        return id;
    }

    public String getDe() {
        return de;
    }

    public String getGroupe() {
        return groupe;
    }
}