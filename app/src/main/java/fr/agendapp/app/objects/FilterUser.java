package fr.agendapp.app.objects;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterUser extends Filter {
    String txt;

    public FilterUser(String txt) {
        super(Filter.USER_TYPE);
        this.txt = txt;
    }

    public boolean correspond(Work w) {
        int i = w.getText().lastIndexOf(txt);
        return i != -1;
    }
}
