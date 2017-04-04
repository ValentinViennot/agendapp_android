package fr.agendapp.app.pages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.factories.SyncFactory;
import fr.agendapp.app.listeners.ClassicListener;
import fr.agendapp.app.objects.User;

/**
 * Réglage des paramètres utilisateur, déconnexion
 * @author Valentin Viennot
 */
public class UserPage extends AppCompatActivity implements ClassicListener {

    User user;

    EditText useremail;
    EditText userprenom;
    EditText usernom;
    AppCompatCheckBox usermail;
    AppCompatCheckBox userrappel;
    Spinner usernotifs;

    // Fenetre de dialogue de "chargement"/"loading"/spinner
    ProgressDialog progressDialog;


    /**
     * On suppose qu'accéder à cette vue nécessite une connexion à Internet (vérification antérieure à l'ouverture)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Définie la vue à utiliser + ActionBar (titre et navigation)
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Récupère les vues utiles
        useremail = (EditText) findViewById(R.id.useremail);
        userprenom = (EditText) findViewById(R.id.userprenom);
        usernom = (EditText) findViewById(R.id.usernom);
        usermail = (AppCompatCheckBox) findViewById(R.id.usermail);
        userrappel = (AppCompatCheckBox) findViewById(R.id.userrappel);
        usernotifs = (Spinner) findViewById(R.id.usernotifs);
        (findViewById(R.id.usersave)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveForm();
                    }
                }
        );
        (findViewById(R.id.userlogout)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logout();
                    }
                }
        );

        // Affiche les données locales en attendant des données récentes
        initForm();

        // Récupère les informations depuis le serveur
        getUserFromServer();

    }

    @Override
    public void onCallBackListener() {
        initForm();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * Mise à jour de l'instance User locale avec la version distante
     */
    private void getUserFromServer() {
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.msg_wait), getResources().getString(R.string.msg_updating));
        // Récupère l'utilisateur depuis le serveur et affiche une notification en cas d'erreur...
        SyncFactory.getInstance(this).getUser(this, this, new NotificationFactory(this));
        // Remarque : La fenêtre de progression ne disparaitra jamais de l'écran...
    }

    /**
     * Initialise les données du formulaire à partir des données sauvegardées de l'utilisateur
     */
    private void initForm() {
        // Récupère une instance de l'utilisateur actif
        user = User.getInstance();
        // S'il n'est pas autorisé à modifier ses informations de compte on l'en empêche
        if (!user.canFakeIdentity()) {
            userprenom.setEnabled(false);
            usernom.setEnabled(false);
        }
        // Mise à jour du contenu des champs
        useremail.setText(user.getEmail());
        userprenom.setText(user.getPrenom());
        usernom.setText(user.getNom());
        usermail.setChecked(user.isMail());
        userrappel.setChecked(user.isRappels());
        usernotifs.setSelection(user.getNotifs() + 1);
    }

    /**
     * Sauvegarde les données de l'utilisateur sur le serveur
     */
    private void saveForm() {
        // Construction des données à envoyer
        String json = "{";
        // Si l'utilisateur peut modifier son nom et prénom
        if (user.canFakeIdentity()) {
            json += "\"prenom\":\"" + userprenom.getText() + "\",";
            json += "\"nom\":\"" + usernom.getText() + "\",";
        }
        json += "\"email\":\"" + useremail.getText() + "\",";
        json += "\"notifs\":" + (usernotifs.getSelectedItemPosition() - 1) + ",";
        json += "\"rappels\":" + (userrappel.isChecked() ? 1 : 0) + ",";
        json += "\"mail\":" + (usermail.isChecked() ? 1 : 0);
        json += "}";
        // Affichage d'une fenetre de chargement
        progressDialog = ProgressDialog.show(this, getResources().getString(R.string.msg_wait), getResources().getString(R.string.msg_updating));
        // Envoi des données au serveur
        SyncFactory.getInstance(this).saveUser(this, this, new NotificationFactory(this), json);
    }

    /**
     * Déconnecte l'utilisateur de l'application (et des APIs)
     */
    private void logout() {
        // On efface les données enregistrées localement
        SharedPreferences preferences = getSharedPreferences(App.TAG, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putString("token", "x");
        editor.apply();
        // On lance une demande d'effacement du token au serveur (silencieuse)
        SyncFactory.getInstance(this).logout(this);
        // On redirige l'utilisateur au travers de l'application
        startActivity(new Intent(this, App.class));
    }

}