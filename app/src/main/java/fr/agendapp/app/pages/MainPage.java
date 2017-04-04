package fr.agendapp.app.pages;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.factories.SyncFactory;

/**
 * Page principale de l'application, contient des éléments de navigation
 *
 * @author Valentin Viennot
 */
public class MainPage extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout / Vue correspondant à cette activité
        setContentView(R.layout.activity_main);
        Log.i(App.TAG, "MainPage created");
        // Ajoute une barre de menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Bouton d'action flottant (pour ajouter un devoir)
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this, NewPage.class));
            }
        });
        setSupportActionBar(toolbar);
        // Prépare la navigation par système d'onglets
        // TODO : Remplacer les onglets par un menu latéral ?
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Ajoute les onglets à la barre de navigation
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Bundle extras = getIntent().getExtras();
        if (extras != null) // Se positionner sur un onglet particulier
            if (extras.containsKey("tab")) {
                int tab = extras.getInt("tab");
                viewPager.setCurrentItem(tab, true);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SelectFilterPage.class));
                return true;
            case R.id.action_params:
                if (!SyncFactory.isOffline()) {
                    startActivity(new Intent(this, UserPage.class));
                    return true;
                } else {
                    NotificationFactory.add(this, 0, "Hors ligne", "Une connexion à Internet est nécessaire pour modifier les paramères.");
                }
                return false;
            case R.id.action_calendar:
                Intent intent = new Intent(this, CalendarPage.class);
                intent.putExtra("archives", viewPager.getCurrentItem() > 0);
                startActivity(intent);
                return true;
        }
        return false;
    }

    // Initialise les "pages" (=Fragments) de chaque onglet
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        // On ajoute la vue "WorkPage" à cette activité (MainPage)
        adapter.addFragment(new WorkPage(), getResources().getString(R.string.nav_devoirs));
        adapter.addFragment(new ArchivesPage(), getResources().getString(R.string.nav_archives));
        viewPager.setAdapter(adapter);
    }

    /**
     * Classe interne Adapter de gestion des onglets et de la navigation par onglets
     */
    private static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
