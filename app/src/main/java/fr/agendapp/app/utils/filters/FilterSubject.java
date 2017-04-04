package fr.agendapp.app.utils.filters;

import fr.agendapp.app.objects.Work;

/**
 * @author Charline Bardin
 */
public class FilterSubject extends Filter {
    private String subject;

    public FilterSubject(String subject) {
        super(Filter.SUBJECT_TYPE);
        this.subject = subject;
    }

    String getSubject() {
        return subject;
    }

    public boolean correspond(Work w) {
        return (w.getSubject().equals(this.subject));
    }
}
