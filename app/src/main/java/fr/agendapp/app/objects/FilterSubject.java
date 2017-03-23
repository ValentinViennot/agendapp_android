package fr.agendapp.app.objects;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterSubject extends Filter {
    String subject;

    public FilterSubject(String subject) {
        super(Filter.SUBJECT_TYPE);
        this.subject = subject;
    }

    public boolean correspond(Work w) {
        return (w.getSubject()==this.subject);
    }
}
