package fr.agendapp.app.utils.filters;

import fr.agendapp.app.objects.Work;

/**
 * @author Charline Bardin
 */
public class FilterAuthor extends Filter {
    private String author;

    public FilterAuthor(String author) {
        super(Filter.AUTHOR_TYPE);
        this.author = author;
    }

    public boolean correspond(Work w) {
        return (w.getAuthor().equals(this.author));
    }

    @Override
    public String toString() {
        return author;
    }
}
