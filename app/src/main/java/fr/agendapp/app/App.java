package fr.agendapp.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import fr.agendapp.app.factories.SyncFactory;
import fr.agendapp.app.pages.LoginPage;
import fr.agendapp.app.pages.MainPage;

/**
 * Classe d'entrée (MAIN) dans l'applicartion = point de départ
 */
public class App extends AppCompatActivity {

    // TODO supprimer Logs de debug
    public static final String TAG = "Agendapp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Méthode déclenchée à la création de l'application (=ouverture)
        super.onCreate(savedInstanceState);
        // Affiche le SplashScreen
        setContentView(R.layout.activity_main);
        // Page où rediriger l'utilisateur
        Intent page;
        // Stockage local (mémoire interne)
        SharedPreferences preferences = getSharedPreferences(TAG, MODE_PRIVATE);
        // On vérifie que les données nécessaires au démarrage sont bien présentes
        if (isLogged(preferences)) {
            Log.i(TAG, "isLogged TRUE");
            // TODO Initialisser l'objet User pareil que Sync
            // Initialise le service de communication avec le serveur d'APIs
            SyncFactory.init(this, preferences.getString("token", "x"));
            // redirige vers la page des devoirs
            page = new Intent(App.this, MainPage.class);
        } else {
            Log.i(TAG, "isLogged FALSE");
            // redirige vers la page d'identification
            page = new Intent(App.this, LoginPage.class);
        }
        // Selon le cas, on redirige l'utilisateur vers la page appropriée
        startActivity(page);
    }

    /**
     * Les données nécessaires au lancement sont le token d'identification aux APIs et un objet User
     * @param preferences LocalStorage
     * @return True si les données nécessaires au démarrage sont présentes, False sinon
     */
    private boolean isLogged(SharedPreferences preferences) {
        return !(
                preferences.getString("user", "x").equals("x") || preferences.getString("token", "x").equals("x")
        );
    }

    // TODO on destroy qui vide la liste de requetes http
}
