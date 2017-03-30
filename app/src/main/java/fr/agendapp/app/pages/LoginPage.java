package fr.agendapp.app.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.SyncFactory;

public class LoginPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO Layout / Vue correspondant à cette activité
        setContentView(R.layout.activity_main);
        Log.i(App.TAG, "LoginPage created");
        // TODO formulaire
        SyncFactory.login(this, "test@agendapp.fr", "test");
    }

    public void onLogin(String token, String message) {
        // TODO Notification.add message
        SharedPreferences preferences = getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
        Intent page = new Intent(LoginPage.this, App.class);
        startActivity(page);
    }

    public void onErrorLogin(String message) {
        // TODO
        Log.w(App.TAG, "message : " + message);
    }
}