package fr.agendapp.app.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.factories.SyncFactory;

/**
 * Page d'identification à l'application
 */
public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText password = (EditText) findViewById(R.id.password);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncFactory.login(LoginPage.this,
                        username.getText().toString(),
                        password.getText().toString());
            }
        });
    }

    /**
     * A la réception d'un résultat positif à la requete de login
     *
     * @param token   Token d'identification aux APIs
     * @param message Message de connexion
     */
    public void onLogin(String token, String message) {
        SharedPreferences preferences = getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
        Intent page = new Intent(LoginPage.this, App.class);
        NotificationFactory.add(this, 0, message, "");
        startActivity(page);
    }

    /**
     * Requete de connexion aux APIs réussie mais echec de connexion
     * @param message Message d'erreur
     */
    public void onErrorLogin(String message) {
        NotificationFactory.add(this, 2, message, "");
    }
}