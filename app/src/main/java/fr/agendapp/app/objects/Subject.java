package fr.agendapp.app.objects;

import android.graphics.Color;

/**
 * Matière
 *
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Subject extends Group {

    Subject() {
        super(2);
    }

    /**
     * Ajoute une couleur personnalisée sur un groupe
     *
     * @param c couleur choisie pour la matière
     * @return true si la couleur est changée par l'utilisateur, false sinon
     */
    boolean setColor(Color c) {
        // Si l'utilisateur n'a pas rejoint le groupe, il ne peur pas en changer la couleur personnalisée
        if (this.isUser()) {
            // TODO
            return false;
        }
        return false;
    }

}