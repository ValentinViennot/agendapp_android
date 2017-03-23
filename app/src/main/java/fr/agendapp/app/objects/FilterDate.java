package fr.agendapp.app.objects;

import java.util.Date;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterDate extends Filter {
    Date date;

    public FilterDate(Date date) {
        super(Filter.DATE_TYPE);
        this.date = date;
    }

    public boolean correspond(Work w) {
        return (w.getDate()==this.date);
    }
}