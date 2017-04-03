package fr.agendapp.app.pages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.R;
import fr.agendapp.app.objects.Work;
import fr.agendapp.app.utils.filters.Filter;
import fr.agendapp.app.utils.filters.SelectableFilter;

/**
 * @author Valentin Viennot
 */
public class FilterPage extends AppCompatActivity implements FilterInterface {

    List<Work> results = new LinkedList<>();

    Adapter adapter;
    SelectableFilter.FilterAdapter applied;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        applied = new SelectableFilter.FilterAdapter(this);

        TextView textApplied = (TextView) findViewById(R.id.filtertextapplied);
        textApplied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryIt();
            }

            private void tryIt() {
                try {
                    Filter.clearFilter();
                    NavUtils.navigateUpFromSameTask(FilterPage.this);
                } catch (ConcurrentModificationException e) {
                    tryIt();
                }
            }
        });
        RecyclerView viewApplied = (RecyclerView) findViewById(R.id.filtersapplied);
        viewApplied.setHasFixedSize(true);
        viewApplied.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        viewApplied.setAdapter(applied);

        RecyclerView resultsList = (RecyclerView) findViewById(R.id.resultsview);
        resultsList.setHasFixedSize(false);
        resultsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter();
        resultsList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        applyFilters();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tosearch, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NavUtils.navigateUpFromSameTask(FilterPage.this);
                return true;
            }
        });
        return true;
    }

    @Override
    public void applyFilters() {
        new Init().execute();
    }

    private class Init extends AsyncTask<Void, Void, Void> {

        // Fenetre de dialogue de "chargement"
        ProgressDialog progressDialog;

        private List<Filter> appliedfilters;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Affiche une fenetre de chargement
            progressDialog = ProgressDialog.show(
                    FilterPage.this,
                    FilterPage.this.getResources().getString(R.string.msg_wait),
                    FilterPage.this.getResources().getString(R.string.msg_updating)
            );
        }

        @Override
        protected Void doInBackground(Void... params) {
            Filter.setSelectables(FilterPage.this);
            appliedfilters = Filter.getActiveFilters();
            results = new LinkedList<>();
            // Récupère les devoirs à venir correspondant aux Filtres
            List<Work> coming = Filter.applyFilters(Work.getComingwork(FilterPage.this));
            // Inverse l'ordre (pour cohérence avec archives)
            Collections.reverse(coming);
            // Ajoute les résultats à la liste de résultats
            results.addAll(coming);
            // Récupère les archives correspondant aux filtres
            List<Work> past = Filter.applyFilters(Work.getPastwork(FilterPage.this));
            // Ajoute les résultats à la liste de résultats
            results.addAll(past);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            applied.setFilters(appliedfilters);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.setResult(results.get(position));
        }

        @Override
        public int getItemCount() {
            return results.size();
        }
    }

    class Holder extends RecyclerView.ViewHolder {

        private TextView text;
        private TextView subject;
        private TextView date;
        private TextView done;
        private TextView comm;
        private ImageView color;
        private ImageView flag;

        private RelativeLayout layout;

        Holder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.object_result, parent, false));
            layout = (RelativeLayout) itemView.findViewById(R.id.result);
            text = (TextView) itemView.findViewById(R.id.resulttext);
            subject = (TextView) itemView.findViewById(R.id.resultsubject);
            date = (TextView) itemView.findViewById(R.id.resultdate);
            done = (TextView) itemView.findViewById(R.id.result_nbDone);
            comm = (TextView) itemView.findViewById(R.id.result_nbComment);
            color = (ImageView) itemView.findViewById(R.id.resultcolor);
            flag = (ImageView) itemView.findViewById(R.id.resultflag);
        }

        @SuppressWarnings("deprecation")
        void setResult(final Work work) {
            Resources r = FilterPage.this.getResources();
            Calendar cal = Calendar.getInstance();
            cal.setTime(work.getDate());
            String d = " " + (cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" : "") + cal.get(Calendar.DAY_OF_MONTH) + "/" + ((cal.get(Calendar.MONTH) + 1 < 10 ? "0" : "") + (cal.get(Calendar.MONTH) + 1));
            text.setText(work.getText());
            String s = work.getSubject() + (work.isDone() ? " (" + r.getString(R.string.fait) + ")" : "");
            subject.setText(s);
            date.setText(r.getString(R.string.pourle, d));
            done.setText(Integer.toString(work.getNbDone()));
            comm.setText(Integer.toString(work.getComments().size()));
            color.setColorFilter(work.getSubjectColor());
            int[] colors = new int[]{
                    r.getColor(R.color.flag_blue),
                    r.getColor(R.color.flag_orange),
                    r.getColor(R.color.flag_red)
            };
            int flag = work.getFlag();
            if (flag > 0 && flag <= colors.length) {
                this.flag.setVisibility(View.VISIBLE);
                this.flag.setColorFilter(colors[flag - 1]);
            } else {
                this.flag.setVisibility(View.INVISIBLE);
            }
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Filter.clearFilter();
                    Intent intent = new Intent(FilterPage.this, MainPage.class);
                    int tab = 0;
                    int pos = 0;
                    pos = Work.getComingwork(FilterPage.this).indexOf(work);
                    if (pos < 0) {
                        tab = 1;
                        pos = Work.getPastwork(FilterPage.this).indexOf(work);
                    }
                    intent.putExtra("tab", tab);
                    intent.putExtra("pos", ++pos);
                    startActivity(intent);
                }
            });
        }
    }
}
