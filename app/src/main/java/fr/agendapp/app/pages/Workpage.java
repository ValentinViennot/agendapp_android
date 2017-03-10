package fr.agendapp.app.pages;

import android.app.Activity;
import android.os.Bundle;

import java.util.LinkedList;

import fr.agendapp.app.objects.*;

/**
 * Page "cahier de texte"
 * @author Dylan Habans
 * @author Valentin Viennot
 * @author Charline Bardin
 * @author Lucas Probst
 */
public class WorkPage extends Activity {

    static String type = "devoirs";
    /** Filtre ajouté par l'utilisateur */
    String filter;
    /** Filtre ajouté par l'ordinateur */
    String autofilter;
    /** Matières disponibles pour le filtrage */
    String[][] subjectsfilter;
    /** Drapeaux disponibles au filtrage */
    String[][] flagsfilter;
    Invite[] invits ;
    LinkedList<Work> homeworks;
    Section[] sections ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void addFilter(String filter) {
        this.filter+="&&"+filter;
    }

}