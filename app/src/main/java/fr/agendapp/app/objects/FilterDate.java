package fr.agendapp.app.objects;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterDate extends Filter {
    Date date;

    FilterDate(Date date) {
        super(Filter.DATE_TYPE);
        this.date = date;
    }

    boolean correspond(Work w) {
        return (w.getDate()==this.date);
    }
}
