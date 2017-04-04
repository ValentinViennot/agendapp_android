package fr.agendapp.app.utils.filters;

import fr.agendapp.app.objects.Work;

/**
 * @author Charline Bardin
 */
public class FilterFlag extends Filter {
    private int flag;

    public FilterFlag(int flag) {
        super(Filter.FLAG_TYPE);
        this.flag = flag;
    }

    int getFlag() {
        return flag;
    }

    public boolean correspond(Work w) {
        return (w.getFlag() == this.flag);
    }
}
