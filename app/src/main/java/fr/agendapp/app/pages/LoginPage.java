package fr.agendapp.app.pages;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import fr.agendapp.app.App;

public class LoginPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO enlever
        Log.i(App.TAG, "LoginPage created");
        // DEBUG
        // On créér nous même les données en attendant la méthode d'initialisation
        SharedPreferences preferences = getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Variables essentielles , créées par le Login
        editor.putString("user", "{\"id\":19,\"prenom\":\"Utilisateur\",\"nom\":\"Test\",\"email\":\"test@agendapp.fr\",\"notifs\":-1,\"rappels\":1,\"mai…");
        editor.putString("token", "1dec26ac0acec38ca4459dc11ae80495");
        // Variables essentielles créées vides à l'initialisation du Sync si non existantes
        editor.putString("version", "8ec2115b893a1aed9730ce0001ec9234");
        editor.putString("devoirs", "[{\"id\":3166,\"user\":19,\"date\":\"2017-03-10T19:00:00.000+02:00\",\"auteur\":\"UtilisateurTest\",\"matiere\":\"P…");
        editor.putString("archives", "[{\"id\":186,\"user\":19,\"date\":\"2017-03-08T19:00:00.000+02:00\",\"auteur\":\"UtilisateurTest\",\"matiere\":\"Ma…");
        // Enregistrement des modifications
        editor.apply();
        Intent page = new Intent(LoginPage.this, MainPage.class);
        startActivity(page);
        // TODO DEBUG
    }
}