package fr.agendapp.app.filters;

public class SelectableFilter {

    private Filter filter;
    private int count;

    public SelectableFilter(Filter filter) {
        this.filter = filter;
        this.count = 0;
    }

    public void inc() {
        count++;
    }

    public void activate() {
        Filter.addFilter(this.filter);
    }

    public void deactivate() {
        Filter.clearFilter(this.filter);
    }
}
