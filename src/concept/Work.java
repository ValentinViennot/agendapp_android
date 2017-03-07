import java.util.date;

/**
 * TODO
 * @author Dylan Habans
 */
class Work {

    static Work[] homeworks = new Work [];
    int id;
    int user;
    String author;
    String subject;
    Color subject_c;
    String text;
    int nbr_done;
    boolean done;
    int flag;
    Comment[] remarks = new Comment [];
    Attachment[] PJ = new Attachment [];

    Work() {

    }
    /**
     * @author Dylan Habans
     * @return true si le devoir est marqué comme fait par l'utilisateur, false sinon
     */
    boolean done (){

        return false;
    }

    /**
     * @author Dylan Habans
     * @return true si le devoir est supprimé par l'utilisateur, false sinon
     */
    boolean delete (){

        return false;

    }

    /**
     * @author Dylan Habans
     * @return true si le devoir est signalé par l'utilisateur, false sinon
     */
    boolean report () {

        return false;

    }

    static getHomeworks (Work[]){

        return Work[];
    }


}