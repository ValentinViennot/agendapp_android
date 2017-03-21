package fr.agendapp.app.objects;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterUser extends Filter {
    String txt;

    FilterUser(String txt) {
        super(Filter.USER_TYPE);
        this.txt = txt;
    }

    boolean correspond(Work w) {
        int i = w.getText().lastIndexOf(txt);
        return i != -1;
    }
}
