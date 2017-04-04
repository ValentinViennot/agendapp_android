package fr.agendapp.app.utils.filters;

import java.util.Calendar;
import java.util.Date;

import fr.agendapp.app.objects.Work;

/**
 * @author Charline Bardin
 */
public class FilterDate extends Filter {
    Date date;

    public FilterDate(Date date) {
        super(Filter.DATE_TYPE);
        this.date = date;
    }

    public boolean correspond(Work w) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(w.getDate());
        return (cal.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public String toString() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return ((cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + cal.get(Calendar.DAY_OF_MONTH)) + "/" + ((cal.get(Calendar.MONTH) + 1 < 10 ? "0" : "") + (cal.get(Calendar.MONTH) + 1));
    }
}