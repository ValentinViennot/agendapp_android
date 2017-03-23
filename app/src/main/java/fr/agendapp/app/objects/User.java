package fr.agendapp.app.objects;

import android.content.Context;
import android.content.SharedPreferences;

import fr.agendapp.app.App;
import fr.agendapp.app.factories.ParseFactory;

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
    private int rappels;
    /**
     * Recevoir des notifications par email ?
     */
    private int mail;
    /**
     * L'utilisateur peut il modifier son prenom et son nom ?
     */
    private int fake_identity;
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
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, Context.MODE_PRIVATE);
        String json = preferences.getString("user", "x");
        if (json.equals("x")) {
            user = null;
        } else {
            user = ParseFactory.parseUser(json);
        }
        // TODO get a new version from server et refresh en callback
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
        if (this.canFakeIdentity()) this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (this.canFakeIdentity()) this.nom = nom;
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
        return rappels > 0;
    }

    public void setRappels(boolean rappels) {
        this.rappels = rappels ? 1 : 0;
    }

    public boolean isMail() {
        return mail > 0;
    }

    public void setMail(boolean mail) {
        this.mail = mail ? 1 : 0;
    }

    public boolean canFakeIdentity() {
        return fake_identity > 0;
    }

    public int getRoot() {
        return root;
    }


}