package fr.agendapp.app.pages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.List;

import fr.agendapp.app.R;
import fr.agendapp.app.utils.filters.Filter;
import fr.agendapp.app.utils.filters.FilterAuthor;
import fr.agendapp.app.utils.filters.FilterDate;
import fr.agendapp.app.utils.filters.FilterDone;
import fr.agendapp.app.utils.filters.FilterSubject;
import fr.agendapp.app.utils.filters.FilterUser;
import fr.agendapp.app.utils.filters.SelectableFilter;

/**
 * @author Valentin Viennot
 */
public class SelectFilterPage extends AppCompatActivity implements FilterInterface {

    private SelectableFilter.FilterAdapter applied;
    private SelectableFilter.FilterAdapter subjects;
    private SelectableFilter.FilterAdapter flags;
    private SelectableFilter.FilterAdapter done;

    private TextView textApplied;
    private RecyclerView viewApplied;
    private SearchView mSearchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectfilter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        applied = new SelectableFilter.FilterAdapter(this);
        done = new SelectableFilter.FilterAdapter(this);
        subjects = new SelectableFilter.FilterAdapter(this);
        flags = new SelectableFilter.FilterAdapter(this);

        textApplied = (TextView) findViewById(R.id.filtertextapplied);
        textApplied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryIt();
            }

            private void tryIt() {
                try {
                    Filter.clearFilter();
                    new Init().execute();
                } catch (ConcurrentModificationException e) {
                    tryIt();
                }
            }
        });
        viewApplied = (RecyclerView) findViewById(R.id.filtersapplied);
        viewApplied.setHasFixedSize(true);
        viewApplied.setLayoutManager(new LinearLayoutManager(SelectFilterPage.this, LinearLayoutManager.HORIZONTAL, false));
        viewApplied.setAdapter(applied);

        RecyclerView v1 = (RecyclerView) findViewById(R.id.filterssubject);
        v1.setHasFixedSize(true);
        v1.setLayoutManager(new LinearLayoutManager(SelectFilterPage.this, LinearLayoutManager.HORIZONTAL, false));
        v1.setAdapter(subjects);

        RecyclerView v2 = (RecyclerView) findViewById(R.id.filtersflag);
        v2.setHasFixedSize(true);
        v2.setLayoutManager(new LinearLayoutManager(SelectFilterPage.this, LinearLayoutManager.HORIZONTAL, false));
        v2.setAdapter(flags);

        RecyclerView v3 = (RecyclerView) findViewById(R.id.filtersdone);
        v3.setHasFixedSize(true);
        v3.setLayoutManager(new LinearLayoutManager(SelectFilterPage.this, LinearLayoutManager.HORIZONTAL, false));
        v3.setAdapter(done);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Init().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchMenuItem.expandActionView();
        return true;
    }

    public void applyFilters() {
        applyFilters(mSearchView.getQuery().toString());
    }

    private void applyFilters(String text) {
        if (text.length() > 0) {
            char type = text.charAt(0);
            Filter filter;
            // Si la chaine de caractères est semblable à une date DD-MM-YYYY, on applique un filtre de date
            if (text.matches("^[0-3]?[0-9]-[0-1]?[0-9]-[0-9]{4}$")) {
                Calendar cal = Calendar.getInstance();
                int day = Integer.parseInt(text.substring(0, 2));
                int month = Integer.parseInt(text.substring(3, 5));
                int year = Integer.parseInt(text.substring(6));
                cal.set(year, month - 1, day);
                filter = new FilterDate(cal.getTime());
            } else
                switch (type) {
                    // Filtre par matière
                    case '#':
                        filter = new FilterSubject(text.substring(1));
                        break;
                    // Filtre par auteur
                    case '@':
                        filter = new FilterAuthor(text.substring(1));
                        break;
                    default:
                        filter = new FilterUser(text);
                }
            Filter.addFilter(filter);
        }
        startActivity(new Intent(this, FilterPage.class));
    }

    private class Init extends AsyncTask<Void, Void, Void> {

        // Fenetre de dialogue de "chargement"
        ProgressDialog progressDialog;

        private List<Filter> doneornot;
        private List<Filter> appliedfilters;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Affiche une fenetre de chargement
            progressDialog = ProgressDialog.show(
                    SelectFilterPage.this,
                    SelectFilterPage.this.getResources().getString(R.string.msg_wait),
                    SelectFilterPage.this.getResources().getString(R.string.msg_updating)
            );
        }

        @Override
        protected Void doInBackground(Void... params) {
            Filter.setSelectables(SelectFilterPage.this);
            doneornot = new ArrayList<>(2);
            doneornot.add(new FilterDone(true));
            doneornot.add(new FilterDone(false));
            appliedfilters = Filter.getActiveFilters();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (appliedfilters.size() > 0) {
                textApplied.setVisibility(View.VISIBLE);
                viewApplied.setVisibility(View.VISIBLE);
                applied.setFilters(appliedfilters);
            } else {
                viewApplied.setVisibility(View.GONE);
                textApplied.setVisibility(View.GONE);
            }
            subjects.setSelectableFilters(Filter.getSubjects());
            flags.setSelectableFilters(Filter.getFlags());
            done.setFilters(doneornot);
            progressDialog.dismiss();
        }
    }

}
