package fr.agendapp.app.pages;

import fr.agendapp.app.objects.Filter;
import fr.agendapp.app.objects.Invite;
import fr.agendapp.app.objects.Section;
import fr.agendapp.app.objects.Work;

import java.util.LinkedList;
import java.util.List;

/**
 * Page "cahier de texte"
 * @author Dylan Habans
 * @author Valentin Viennot
 * @author Charline Bardin
 * @author Lucas Probst
 */
public class WorkPage extends Activity {

    static String type = "devoirs";
    /**
     * Filtre ajouté par l'utilisateur
     */
    Filter filter;
    /**
     * Filtre ajouté par l'ordinateur
     */
    String autofilter;

    Invite[] invits;
    List<Work> homeworks;
    Section[] sections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Méthode qui renvoie une liste de devoirs après avoir appliqué un filtre
     *
     * @return : la liste de devoirs filtrés
     */
    private List<Work> applyFilter() {
        List<Work> res = new LinkedList<>();
        for (fr.agendapp.app.objects.Work w : homeworks) {
            boolean intermediaire = false;
            for (String s : filter.getMatieres()) {
                if (w.getMatiere().contains(s) || filter.getMatieres() == null) {
                    intermediaire = true;
                    break;
                }
            }
            if (intermediaire) {
                for (Integer i : filter.getFlag()) {
                    if (w.getFlag() == i || filter.getFlag() == null) {
                        intermediaire = true;
                        break;
                    } else {
                        intermediaire = false;
                    }
                }

            }
            if (intermediaire) {
                if ((filter.isFait() == w.isFait()) && (filter.getAuteur().indexOf(w.getAuteur()) != -1 || filter.getAuteur() == null) &&
                        (filter.getResearch().indexOf(w.getText()) != -1 || filter.getResearch() == null)) {
                    res.add(w);
                }

            }

        }
        return res;
    }

}

