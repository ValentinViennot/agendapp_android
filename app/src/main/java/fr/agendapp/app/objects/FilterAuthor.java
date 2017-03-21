package fr.agendapp.app.objects;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterAuthor extends Filter {
    int user;

    FilterAuthor(int user) {
        super(Filter.AUTHOR_TYPE);
        this.user = user;
    }

    boolean correspond(Work w) {
        return (w.getUser()==this.user);
    }
}