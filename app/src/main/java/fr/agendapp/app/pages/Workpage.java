/**
 * Page "cahier de texte"
 * @author Dylan Habans
 * @author Valentin Viennot
 * @author Charline Bardin
 * @author Lucas Probst
 */
class Workpage {

    static String type = "devoirs";
    /** Filtre ajouté par l'utilisateur */
    String filter;
    /** Filtre ajouté par l'ordinateur */
    String autofilter;
    /** Matières disponibles pour le filtrage */
    String[][] subjectsfilter = new String [][2];
    /** Drapeaux disponibles au filtrage */
    String[][] flagsfilter = new String [][2];
    Invite[] invits ;
    LinkedList<Work> homeworks;
    Section[] sections ;

    Workpage() {

    }

    private addFilter(String filter) {
        filtre+="&&"+filter;
    }

}