package fr.agendapp.app.filters;

import fr.agendapp.app.objects.Work;

/**
 * Created by Charline on 21/03/2017.
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

}
