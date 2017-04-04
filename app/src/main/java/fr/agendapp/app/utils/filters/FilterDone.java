package fr.agendapp.app.utils.filters;

import fr.agendapp.app.objects.Work;

/**
 * @author Charline Bardin
 */
public class FilterDone extends Filter {
    boolean done;

    public FilterDone(boolean done) {
        super(Filter.DONE_TYPE);
        this.done = done;
    }

    public boolean correspond(Work w) {
        return (w.isDone() == this.done);
    }

    @Override
    public String toString() {
        //TODO resources
        return done ? "Faits" : "À faire";
    }
}
