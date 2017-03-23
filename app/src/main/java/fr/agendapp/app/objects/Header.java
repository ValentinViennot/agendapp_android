package fr.agendapp.app.objects;

public class Header {

    private int from;
    private int to;
    private String title;

    public Header(int from, String title) {
        this.from = from;
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }
}
