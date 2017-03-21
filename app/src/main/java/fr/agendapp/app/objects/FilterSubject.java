package fr.agendapp.app.objects;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterSubject extends Filter {
    String subject;

    FilterSubject(String subject) {
        super(Filter.SUBJECT_TYPE);
        this.subject = subject;
    }

    boolean correspond(Work w) {
        return (w.getSubject()==this.subject);
    }
}
