package fr.agendapp.app.objects;

import android.content.Context;

/**
 * TODO
 * @author Dylan Habans
 */
public class User {

    /**
     * Utilisateur connecté
     */
    private static User user;

    /**
     * ID dans la base
     */
    private int id;
    private String prenom;
    private String nom;
    /**
     * Adresse email
     */
    private String email;
    /**
     * Heure de rappel des devoirs (-1 si désactivé)
     */
    private int notifs;
    /**
     * Notifications pour les ajouts
     */
    private boolean rappels;
    /**
     * Recevoir des notifications par email ?
     */
    private boolean mail;
    /**
     * L'utilisateur peut il modifier son prenom et son nom ?
     */
    private boolean fake_identity;
    /**
     * Dossier racine de l'utilisateur
     */
    private int root;
    /**
     * Matières auxquelles l'utilisateur est abonné
     */
    private Subject[] courses;

    public User() {

    }

    /**
     * Déconnecte l'utilisateur actif
     * - Suppression des données locales
     * - Appel à l'API logout
     * - user=null;
     * - Redirection vers MainActivity
     *
     * @param b Déconnexion de partout ? (toutes les sessions)
     */
    public static void logout(boolean b) {
        // TODO
    }

    /**
     * Initialise l'utilisateur actuel
     */
    public static void init(Context context) {
        // TODO
        // init from local storage
        // get a new version from server
    }

    /**
     * @return Utilisateur actif
     */
    public static User getInstance() {
        // TODO seule cette méthode et init devraient etre statique
        // TODO devrait retourner null si rien de sauvegardé...
        // TODO comme sync
        return user;
    }

    /**
     * Envoie l'utilisateur actif au serveur pour traitement des modifications
     *
     * @return true si réussi, false sinon
     */
    public static boolean save() {
        //TODO
        return false;
    }

    // GETTERS AND SETTERS
    // On peut modifier localement un utilisateur
    // Et ensuite envoyer l'objet ainsi modifié au serveur

    public int getId() {
        return id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        if (fake_identity) this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (fake_identity) this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNotifs() {
        return notifs;
    }

    public void setNotifs(int notifs) {
        this.notifs = notifs;
    }

    public boolean isRappels() {
        return rappels;
    }

    public void setRappels(boolean rappels) {
        this.rappels = rappels;
    }

    public boolean isMail() {
        return mail;
    }

    public void setMail(boolean mail) {
        this.mail = mail;
    }

    public boolean canFakeIdentity() {
        return fake_identity;
    }

    public int getRoot() {
        return root;
    }


}