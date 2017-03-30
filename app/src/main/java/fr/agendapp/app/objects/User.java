package fr.agendapp.app.objects;

import android.content.Context;
import android.content.SharedPreferences;

import fr.agendapp.app.App;
import fr.agendapp.app.factories.ParseFactory;
import fr.agendapp.app.factories.SyncFactory;
import fr.agendapp.app.listeners.ClassicListener;

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

    public static void clear() {
        user = new User();
    }

    /**
     * Initialise l'utilisateur actuel avec les données locales
     */
    public static void init(final Context context) {
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, Context.MODE_PRIVATE);
        String json = preferences.getString("user", "x");
        if (json.equals("x")) {
            // Evitons un NullPointer
            user = new User();
        } else {
            // Si les données sont valides, on utilise les données locales
            // Celles distantes seront récupérées plus tard, si besoin
            user = ParseFactory.parseUser(json);
        }
    }

    /**
     * Initialise l'utilisateur actuel
     *
     * @param b True s'il faut lancer une synchronisation silencieuse de l'utilisateur
     */
    public static void init(final Context context, boolean b) {
        if (b) {
            // On tente de récupérer une version récente depuis le serveur
            SyncFactory.getInstance(context).getUser(context, new ClassicListener() {
                @Override
                public void onCallBackListener() {
                    // Une fois la version récente récupérée, on initialisera avec les nouvelles données
                    init(context);
                }
            }, null);
        }
        // En attendant, on initialise avec les données locales
        init(context);
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