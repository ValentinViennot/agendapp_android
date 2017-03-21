package fr.agendapp.app.objects;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterDone extends Filter {
    boolean done;

    FilterDone(boolean done) {
        super(Filter.DONE_TYPE);
        this.done = done;
    }

    boolean correspond(Work w) {
        return (w.isDone()==this.done);
    }
}
