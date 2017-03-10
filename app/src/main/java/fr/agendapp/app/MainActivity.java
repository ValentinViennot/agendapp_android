package fr.agendapp.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import fr.agendapp.app.pages.WorkPage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO enlever
        // DEBUG
        // On créér nous même les données en attendant la méthode d'initialisation
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Variables essentielles , créées par le Login
        editor.putString("user","{\"id\":19,\"prenom\":\"Utilisateur\",\"nom\":\"Test\",\"email\":\"test@agendapp.fr\",\"notifs\":-1,\"rappels\":1,\"mai…");
        editor.putString("token","1dec26ac0acec38ca4459dc11ae80495");
        // Variables essentielles créées vides à l'initialisation du Sync si non existantes
        editor.putString("version","8ec2115b893a1aed9730ce0001ec9234");
        editor.putString("devoirs","[{\"id\":3166,\"user\":19,\"date\":\"2017-03-10T19:00:00.000+02:00\",\"auteur\":\"UtilisateurTest\",\"matiere\":\"P…");
        editor.putString("archives","[{\"id\":186,\"user\":19,\"date\":\"2017-03-08T19:00:00.000+02:00\",\"auteur\":\"UtilisateurTest\",\"matiere\":\"Ma…");
        // Enregistrement des modifications
        editor.apply();
        // TODO DEBUG
        // On vérifie que les données nécessaires au démarrage sont bien présentes
        if (isLogged()) {
            Log.i(TAG,"isLogged TRUE : DEBUG");
            // Initialisé Sync avec le token (+ initialise les variables + lance une synchro différée)
            Intent workpage = new Intent(MainActivity.this, WorkPage.class);
            startActivity(workpage);
        } else {
            // TODO
            Log.w(TAG,"isLogged FALSE");
        }
        // Selon le cas, on redirige l'utilisateur vers la page approprié

    }

    private boolean isLogged() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return (
            !preferences.getString("user","x").equals("x")
            &&!preferences.getString("token","x").equals("x")
        );
    }
}
