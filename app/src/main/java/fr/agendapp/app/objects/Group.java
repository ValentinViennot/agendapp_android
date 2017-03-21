package fr.agendapp.app.objects;

import android.graphics.Color;

/**
 * Groupe : peut être un dossier ou une matière
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public abstract class Group {

    /** ID dans la base */
    private int id;
    /** ID du groupe parent */
    private int parentid;
    /** Nom du groupe */
    private String nom;
    /**
     * Type de groupe (héritage)
     * 0 : Invisible
     * 1 : Dossier
     * 2 : Matière
     */
    private int type;
    /** Couleur du groupe (personnalisée) */
    private Color color;
    /** True si l'utilisateur est inscrit au groupe */
    private boolean isUser;

    public Group(int type) {
        this.type = type;
        this.isUser = true;
    }

    /**
     * Création d'un nouveau Groupe (matière ou dossier)
     * Renvoi l'objet local créé et l'envoi au serveur (async) pour traitement
     *
     * @param parentid ID du groupe conteneur
     * @param nom      Nom du groupe à créer
     * @param type     1 Dossier, 2 Matière, 0 invisible
     * @return Groupe nouvellement créé , null si erreur
     */
    public static Group newGroup(
            int parentid,
            String nom,
            int type
    ) {
        // TODO
        // Création du groupe local et envoi sur le serveur
        if (type == 1) return new Directory();
        else return new Subject();
    }

    /**
     * Rejoindre le groupe
     * @return true si l'utilisateur rejoint le groupe, false sinon
     */
    public boolean join() {
        // TODO
        return false;
    }

    // GETTERS

    /**
     * Quitter le groupe
     * @return true si réussite
     */
    public boolean quit() {
        // TODO
        return false;
    }

    public int getId() {
        return id;
    }

    public int getParentid() {
        return parentid;
    }

    public String getNom() {
        return nom;
    }

    public int getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    // Méthodes statiques

    public boolean isUser() {
        return isUser;
    }
}