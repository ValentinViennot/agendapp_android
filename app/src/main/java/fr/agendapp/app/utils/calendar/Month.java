package fr.agendapp.app.utils.calendar;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Valentin on 04/04/2017.
 */

public class Month {

    private List<Week> weeks;
    private String title;

    public Month(String title) {
        this.title = title;
        this.weeks = new LinkedList<>();
    }

    public void add(Week week) {
        weeks.add(week);
    }

    public String getTitle() {
        return title;
    }

    public List<Week> getWeeks() {
        return weeks;
    }
}
