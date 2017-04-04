package fr.agendapp.app.utils.calendar;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.R;
import fr.agendapp.app.pages.FilterPage;
import fr.agendapp.app.utils.filters.Filter;
import fr.agendapp.app.utils.filters.FilterDate;

/**
 * Created by Valentin on 04/04/2017.
 */

public class Week {

    private Day[] days;

    public Week() {
        days = new Day[8];
    }

    public void add(Day day) {
        days[day.getDayOfWeek()] = day;
    }

    /**
     * Si le dimanche est complété alors la semaine est terminée
     *
     * @return true si la semaine est pleine
     */
    public boolean isFull() {
        return days[Calendar.SUNDAY] != null;
    }


    // TODO jours de la semaine ? Passer par 7* include plutot ?
    public static class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.Holder> {

        List<Week> weeks = new LinkedList<>();
        Activity activity;

        public WeekAdapter(List<Week> weeks, Activity activity) {
            this.weeks = weeks;
            this.activity = activity;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind(weeks.get(position));
        }

        @Override
        public int getItemCount() {
            return weeks.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            private TextView lundi;
            private TextView mardi;
            private TextView mercredi;
            private TextView jeudi;
            private TextView vendredi;
            private TextView samedi;
            private TextView dimanche;

            Holder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.object_week, parent, false));
                lundi = (TextView) itemView.findViewById(R.id.weeklundi);
                mardi = (TextView) itemView.findViewById(R.id.weekmardi);
                mercredi = (TextView) itemView.findViewById(R.id.weekmercredi);
                jeudi = (TextView) itemView.findViewById(R.id.weekjeudi);
                vendredi = (TextView) itemView.findViewById(R.id.weekvendredi);
                samedi = (TextView) itemView.findViewById(R.id.weeksamedi);
                dimanche = (TextView) itemView.findViewById(R.id.weekdimanche);
            }

            void bind(Week week) {
                RecyclerView view;
                view = (RecyclerView) itemView.findViewById(R.id.wlurv);
                initDay(week.days[Calendar.MONDAY], lundi, view);
                view = (RecyclerView) itemView.findViewById(R.id.wmarv);
                initDay(week.days[Calendar.TUESDAY], mardi, view);
                view = (RecyclerView) itemView.findViewById(R.id.wmerv);
                initDay(week.days[Calendar.WEDNESDAY], mercredi, view);
                view = (RecyclerView) itemView.findViewById(R.id.wjerv);
                initDay(week.days[Calendar.THURSDAY], jeudi, view);
                view = (RecyclerView) itemView.findViewById(R.id.wverv);
                initDay(week.days[Calendar.FRIDAY], vendredi, view);
                view = (RecyclerView) itemView.findViewById(R.id.wsarv);
                initDay(week.days[Calendar.SATURDAY], samedi, view);
                view = (RecyclerView) itemView.findViewById(R.id.wdirv);
                initDay(week.days[Calendar.SUNDAY], dimanche, view);
            }

            private void initRecycler(RecyclerView view, Day.DayAdapter adapter) {
                view.setHasFixedSize(true);
                view.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
                view.setAdapter(adapter);
            }

            private void initDay(@Nullable final Day day, TextView title, final RecyclerView view) {

                String t;
                List<Integer> colors;
                if (day == null) {
                    t = "";
                    colors = new LinkedList<>();
                } else {
                    t = day.getTitle();
                    colors = day.getColors();
                }
                title.setText(t);
                initRecycler(view, new Day.DayAdapter(colors));
                if (day != null)
                    ((LinearLayout) view.getParent()).setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Filter.addFilter(new FilterDate(day.getDate()));
                                    activity.startActivity(new Intent(activity, FilterPage.class));
                                }
                            }
                    );
            }

        }

    }
}
