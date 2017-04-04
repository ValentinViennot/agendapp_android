package fr.agendapp.app.utils.calendar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.R;
import fr.agendapp.app.factories.DateFactory;
import fr.agendapp.app.objects.Work;

/**
 * @author Valentin Viennot
 */

public class Day {

    private List<Integer> colors;
    private Date date;

    private int dayOfWeek;

    private String title;

    public Day(Context context, Date date) {
        this.date = date;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String day = DateFactory.getWeekName(context, dayOfWeek);
        this.title = day.substring(0, 3) + ". " + c.get(Calendar.DAY_OF_MONTH);
        this.colors = new LinkedList<>();
    }

    public void add(Work w) {
        colors.add(w.getSubjectColor());
    }

    public List<Integer> getColors() {
        return colors;
    }

    public Date getDate() {
        return date;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public String getTitle() {
        return title;
    }

    static class DayAdapter extends RecyclerView.Adapter<DayAdapter.Holder> {

        List<Integer> colors;

        public DayAdapter(List<Integer> colors) {
            this.colors = colors;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind(colors.get(position));
        }

        @Override
        public int getItemCount() {
            return colors.size();
        }

        static class Holder extends RecyclerView.ViewHolder {

            private ImageView tag;

            Holder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.object_workday, parent, false));
                tag = (ImageView) itemView.findViewById(R.id.workday);
            }

            void bind(Integer color) {
                tag.setColorFilter(color);
            }
        }
    }
}
