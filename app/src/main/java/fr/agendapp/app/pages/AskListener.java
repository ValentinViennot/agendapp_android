package fr.agendapp.app.pages;

public interface AskListener {

    /**
     * Méthode à exécuter lors de l'appui sur le bouton de validation d'une fenetre de dialogue
     */
    void onAskOk();

    /**
     * Méthode à exécuter lors de l'appui sur le bouton de refus d'une fenetre de dialogue
     */
    void onAskCancel();

}
