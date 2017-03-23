package fr.agendapp.app.pages;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.SyncFactory;

public class LoginPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO Layout / Vue correspondant à cette activité
        setContentView(R.layout.activity_work);
        Log.i(App.TAG, "LoginPage created");
        // TODO formulaire
        SyncFactory.login(this, "test@agendapp.fr", "test");
    }

    public void onLogin(String token, String message) {
        // TODO Notification.add message
        SharedPreferences preferences = getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);

        // TODO remplacer par User.init dans App
        editor.putString("user", "{\"id\":19,\"prenom\":\"Utilisateur\",\"nom\":\"Test\",\"email\":\"test@agendapp.fr\",\"notifs\":-1,\"rappels\":1,\"mail\":0,\"fake_identity\":0,\"root\":40,\"courses\":[{\"id\":43,\"parentid\":null,\"nom\":\"Mes t\\u00e2ches persos\",\"parent\":null,\"type\":2,\"color\":null},{\"id\":180,\"parentid\":177,\"nom\":\"Chimie\",\"parent\":\"TS1\",\"type\":2,\"color\":\"33CC33\"},{\"id\":178,\"parentid\":177,\"nom\":\"Maths\",\"parent\":\"TS1\",\"type\":2,\"color\":\"0066CC\"},{\"id\":181,\"parentid\":177,\"nom\":\"Philosophie\",\"parent\":\"TS1\",\"type\":2,\"color\":\"FF0066\"},{\"id\":179,\"parentid\":177,\"nom\":\"Physique\",\"parent\":\"TS1\",\"type\":2,\"color\":669900}]}");

        editor.apply();
        Intent page = new Intent(LoginPage.this, App.class);
        startActivity(page);
    }

    public void onErrorLogin(String message) {
        // TODO
        Log.w(App.TAG, "message : " + message);
    }
}