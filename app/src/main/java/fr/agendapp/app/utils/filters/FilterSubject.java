package fr.agendapp.app.utils.filters;

import fr.agendapp.app.objects.Work;

public class FilterSubject extends Filter {
    String subject;

    public FilterSubject(String subject) {
        super(Filter.SUBJECT_TYPE);
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public boolean correspond(Work w) {
        return (w.getSubject().equals(this.subject));
    }
}
