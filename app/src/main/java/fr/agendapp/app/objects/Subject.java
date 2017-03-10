package fr.agendapp.app.objects;

import android.graphics.Color;

/**
 * Matière
 * @author Dylan Habans
 * @author Valentin Viennot
 */
class Subject extends Group {

    Subject() {
        super(2);
    }

    /**
     * @param c couleur choisie pour la matière
     * @return true si la couleur ets changée par l'utilisateur, false sinon
     */
    boolean setColor(Color c){

        return false ;
    }

}