package fr.agendapp.app.utils.filters;

import fr.agendapp.app.objects.Work;

/**
 * Filtre personnalisé (chaine de caractères)
 * @author Charline Bardin
 */
public class FilterUser extends Filter {

    private String txt;

    public FilterUser(String txt) {
        super(Filter.USER_TYPE);
        this.txt = txt;
    }

    public boolean correspond(Work w) {
        return w.getText().contains(txt);
    }

    @Override
    public String toString() {
        return txt;
    }
}
