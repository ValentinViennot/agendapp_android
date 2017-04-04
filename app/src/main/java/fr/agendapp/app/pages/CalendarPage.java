package fr.agendapp.app.pages;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.R;
import fr.agendapp.app.factories.DateFactory;
import fr.agendapp.app.objects.Work;
import fr.agendapp.app.utils.WrapLinearLayout;
import fr.agendapp.app.utils.calendar.Day;
import fr.agendapp.app.utils.calendar.Month;
import fr.agendapp.app.utils.calendar.Week;

/**
 * Vue Calendrier
 *
 * @author Valentin Viennot
 */
public class CalendarPage extends AppCompatActivity {

    /** True si le calendrier affiche les archives, false s'il affiche les devoirs */
    boolean archives;
    /** Vue contenant la liste des mois */
    RecyclerView view;

    Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        archives = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) // Se positionner sur un onglet particulier
            if (extras.containsKey("archives"))
                archives = extras.getBoolean("archives");
        view = (RecyclerView) findViewById(R.id.calmonths);
        adapter = new Adapter();
        view.setHasFixedSize(false);
        view.setLayoutManager(new WrapLinearLayout(this));
        view.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Init().execute();
    }

    /**
     * Tâche permettant de charger les données pour la vue
     * Calcule les jours, semaines et mois contenant les devoirs
     */
    private class Init extends AsyncTask<Void, Void, Void> {

        // Fenetre de dialogue de "chargement"
        ProgressDialog progressDialog;

        List<Month> months;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Affiche une fenetre de chargement
            progressDialog = ProgressDialog.show(
                    CalendarPage.this,
                    CalendarPage.this.getResources().getString(R.string.msg_wait),
                    CalendarPage.this.getResources().getString(R.string.msg_updating)
            );
        }

        @Override
        protected Void doInBackground(Void... params) {
            months = new LinkedList<>();
            List<Work> works = new LinkedList<>();
            Calendar cursor = Calendar.getInstance();
            // Récupère la bonne liste de devoirs
            if (archives) {
                works.addAll(Work.getPastwork(CalendarPage.this));
                Collections.reverse(works);
                if (works.size() > 0)
                    cursor.setTime(works.get(0).getDate());
            } else {
                works = Work.getComingwork(CalendarPage.this);
            }
            if (works.size() > 0) {
                // Calendrier pour comparer la date du devoir
                Calendar cal = Calendar.getInstance();
                // Mois en cours
                Month month = new Month(DateFactory.getMonthName(CalendarPage.this, cursor.get(Calendar.MONTH)));
                // Semaine en cours
                Week week = new Week();
                // Jour en cours
                Day day = new Day(CalendarPage.this, cursor.getTime());
                // Remplissons le calendrier jusqu'à la fin de la liste de devoirs
                for (Work w : works) {
                    // On compare à la date d'échéance du devoir
                    cal.setTime(w.getDate());
                    // Tant que la date du devoir est différente de la date en cours
                    while (cursor.get(Calendar.MONTH) != cal.get(Calendar.MONTH)
                            || cursor.get(Calendar.DATE) != cal.get(Calendar.DATE)) {
                        // On vérifie si la semaine n'est pas pleine
                        if (week.isFull()) {
                            // Sinon on passe à une semaine suivante
                            month.add(week);
                            week = new Week();
                        }
                        int oldmonth = cursor.get(Calendar.MONTH);
                        cursor.add(Calendar.DATE, +1);
                        // Si le mois a changé, on change de mois
                        if (cursor.get(Calendar.MONTH) != oldmonth) {
                            months.add(month);
                            month = new Month(DateFactory.getMonthName(CalendarPage.this, cursor.get(Calendar.MONTH)));
                        }
                        // On passe à la date suivante
                        // On ajoute le jour à la semaine en cours
                        week.add(day);
                        // On change de jour
                        day = new Day(CalendarPage.this, cursor.getTime());
                    }
                    // Une fois que la date en cours vaut la date du devoir
                    // On ajoute le devoir au jour en cours
                    day.add(w);
                }
                if (week.isFull()) {
                    month.add(week);
                    week = new Week();
                }
                week.add(day);
                month.add(week);
                months.add(month);
                // La liste de mois est prête !
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.bind(months);
            progressDialog.dismiss();
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        List<Month> months;

        Adapter() {
            super();
            months = new LinkedList<>();
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind(months.get(position));
        }

        @Override
        public int getItemCount() {
            return months.size();
        }

        void bind(List<Month> months) {
            this.months = months;
            notifyDataSetChanged();
        }

        class Holder extends RecyclerView.ViewHolder {

            private TextView title;

            Holder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.object_month, parent, false));
                title = (TextView) itemView.findViewById(R.id.monthtitle);
            }

            void bind(Month month) {
                // Nom du mois
                title.setText(month.getTitle());
                // Liste des semaines
                RecyclerView view = (RecyclerView) itemView.findViewById(R.id.monthweeks);
                Week.WeekAdapter weeks = new Week.WeekAdapter(month.getWeeks(), CalendarPage.this);
                view.setHasFixedSize(true);
                view.setLayoutManager(new WrapLinearLayout(CalendarPage.this) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                view.setAdapter(weeks);
            }

        }
    }

}