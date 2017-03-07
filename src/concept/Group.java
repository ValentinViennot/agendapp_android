/**
 * Groupe : peut être un dossier ou une matière
 * @author Dylan Habans
 * @author Valentin Viennot
 */
abstract class Group{

    /** ID dans la base */
    int id;
    /** ID du groupe parent */
    int parentid;
    /** Nom du groupe */
    String name;
    /** Type de groupe (héritage) */
    int type;
    /** Couleur du groupe (personnalisée) */
    Color color;
    /** True si l'utilisateur est inscrit au groupe */
    boolean isUser;

    Group(int type) {
        this.type = type;
    }

    /**
     * Rejoindre le groupe
     * @return true si l'utilisateur rejoint le groupe, false sinon
     */
    boolean join() {
        return false;
    }

    /**
     * Quitter le groupe
     * @return true si réussite
     */
    boolean quit() {
        return false;
    }
}