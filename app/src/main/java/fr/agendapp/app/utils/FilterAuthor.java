package fr.agendapp.app.utils;

import fr.agendapp.app.objects.Work;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterAuthor extends Filter {
    int user;

    public FilterAuthor(int user) {
        super(Filter.AUTHOR_TYPE);
        this.user = user;
    }

    public boolean correspond(Work w) {
        return (w.getUser()==this.user);
    }
}
