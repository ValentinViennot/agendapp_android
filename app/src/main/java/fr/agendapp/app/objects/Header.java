package fr.agendapp.app.objects;

/**
 * En tete de section de devoirs (mois ou jour)
 */
public class Header {

    /**
     * index du devoir correspondant au début de la section (inclusive)
     */
    private int from;
    /** index du devoir correspondant à la fin de la section (exclusive) */
    private int to;
    /** Titre de l'en tete */
    private String title;

    /**
     * @param from Index du devoir correspondant au début de la section dans la liste de devoirs (inclusif)
     * @param title Titre de la section (exemple : "Janvier" ou "Mardi 15")
     */
    public Header(int from, String title) {
        this.from = from;
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }
}
