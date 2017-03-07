import java.util.date;

/**
 * Représente un devoir
 * @author Dylan Habans
 * @author Valentin Viennot
 */
class Work {

    /** Tableau de devoirs à faire */
    static LinkedList<Work> homeworks;
    /** Tableau des archives */
    static LinkedList<Work> archives;

    /** ID dans la base */
    int id;
    /** ID de l'auteur */
    int user;
    /** "@prenomnom" de l'auteur */
    String author;
    /** Nom de la matière */
    String subject;
    /** Couleur associée la matière */
    Color subject_c;
    /** Texte du devoir */
    String text;
    /** Nombre de marqué comme faits */
    int nbr_done;
    /** Utilisateur a marqué comme fait ? */
    boolean done;
    /** Drapeau attaché par l'utilisateur */
    int flag;
    /** Liste de commentaires */
    ArrayList<Comments> remarks;
    /** Liste de pièces jointes */
    ArrayList<Attachment> attachments;

    Work() {

    }
    /**
     * Marque comme fait/non fait selon le statut actuel
     * @return true si le devoir est marqué comme fait par l'utilisateur, false sinon
     */
    boolean done (){
        return false;
    }

    /**
     * Supprime le devoir
     * @return true si le devoir est supprimé par l'utilisateur, false sinon
     */
    boolean delete (){

        return false;

    }

    /**
     * Signale le devoir au modérateur
     * @return true si le devoir est signalé par l'utilisateur, false sinon
     */
    boolean report () {

        return false;

    }


}