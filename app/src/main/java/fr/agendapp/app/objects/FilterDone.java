package fr.agendapp.app.objects;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterDone extends Filter {
    boolean done;

    public FilterDone(boolean done) {
        super(Filter.DONE_TYPE);
        this.done = done;
    }

    public boolean correspond(Work w) {
        return (w.isDone()==this.done);
    }
}
