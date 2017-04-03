package fr.agendapp.app.pages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.R;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.factories.SyncFactory;
import fr.agendapp.app.listeners.ClassicListener;
import fr.agendapp.app.objects.Subject;
import fr.agendapp.app.objects.User;
import fr.agendapp.app.objects.Work;
import fr.agendapp.app.utils.pending.PendADD;

public class NewPage extends AppCompatActivity implements ClassicListener {

    User user;

    Spinner subject;
    EditText text;
    DatePicker date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Définie la vue à utiliser + ActionBar (titre et navigation)
        setContentView(R.layout.activity_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        subject = (Spinner) findViewById(R.id.newsubject);
        text = (EditText) findViewById(R.id.newtext);
        date = (DatePicker) findViewById(R.id.newdate);
        findViewById(R.id.newsave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        initForm();

        getUserFromServer();
    }

    /**
     * Mise à jour de l'instance User locale avec la version distante
     */
    private void getUserFromServer() {
        // Récupère l'utilisateur depuis le serveur si possible
        SyncFactory.getInstance(this).getUser(this, this, null);
    }

    private void initForm() {
        // Récupération de l'utilisateur
        user = User.getInstance();
        // Récupération des noms des matières auxquelles il est abonné
        List<String> c = new LinkedList<>();
        for (Subject s : user.getSubjects())
            c.add(s.getNom());
        // Définition de la liste de choix de matière
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, c);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(spinnerArrayAdapter);

    }

    @Override
    public void onCallBackListener() {
        initForm();
    }

    private void save() {
        final int MIN_TEXT = 3;
        String text = this.text.getText().toString();
        if (text.length() > MIN_TEXT) {
            user = User.getInstance();
            Calendar c = Calendar.getInstance();
            c.set(date.getYear(), date.getMonth(), date.getDayOfMonth(), 18, 0);
            new PendADD(this,
                    new Work(user, user.getSubjects()[subject.getSelectedItemPosition()], text, c.getTime())
            );
            Intent page = new Intent(this, MainPage.class);
            page.putExtra("delay", 2000);
            startActivity(page);
        } else {
            // TODO resources
            NotificationFactory.add(this, 1, "Trop court", "La description du devoir doit faire plus de " + MIN_TEXT + " caractères.");
        }
    }
}