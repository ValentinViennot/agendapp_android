package fr.agendapp.app.utils;

import fr.agendapp.app.objects.Work;

/**
 * Created by Charline on 21/03/2017.
 */
public class FilterFlag extends Filter {
    int flag;

    public FilterFlag(int flag) {
        super(Filter.FLAG_TYPE);
        this.flag = flag;
    }

    public boolean correspond(Work w) {
        return (w.getFlag()==this.flag);
    }
}
