package fr.agendapp.app.pages;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import fr.agendapp.app.App;
import fr.agendapp.app.factories.SyncFactory;

public class LoginPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(App.TAG, "LoginPage created");
        // TODO formulaire
        SyncFactory.login(this, "test@agendapp.fr", "test");
    }

    public void onLogin(String token, String message) {
        // TODO Notification.add message
        SharedPreferences preferences = getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);

        // TODO remplacer par User.init
        editor.putString("user", "{\"id\":19,\"prenom\":\"Utilisateur\",\"nom\":\"Test\",\"email\":\"test@agendapp.fr\",\"notifs\":-1,\"rappels\":1,\"mai…");

        editor.apply();
        Intent page = new Intent(LoginPage.this, App.class);
        startActivity(page);
    }

    public void onErrorLogin(String message) {
        // TODO
        Log.w(App.TAG, "message : " + message);
    }
}