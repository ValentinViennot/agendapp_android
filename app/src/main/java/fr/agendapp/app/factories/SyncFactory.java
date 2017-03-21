package fr.agendapp.app.factories;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.agendapp.app.App;
import fr.agendapp.app.objects.Work;
import fr.agendapp.app.pages.LoginPage;
import fr.agendapp.app.pages.SyncListener;

public class SyncFactory {

    private static final String baseUrl = "https://apis.agendapp.fr/";
    /**
     * Instance active du service de synchronisation
     */
    private static SyncFactory instance = null;
    /**
     * Token d'identification aux APIs
     */
    private static String token;
    /** File d'attente des requêtes HTTP (Pile du Thread HTTP) */
    private RequestQueue mRequestQueue;
    /** Callback universel en cas d'erreur avec une requête http */
    private Response.ErrorListener errorListener;

    // TODO gestion de l'hors connexion ?

    /**
     * Création d'une nouvelle instance du service de synchronisation
     *
     * @param context Android
     * @param t       Token API
     */
    private SyncFactory(Context context, String t) {
        // Evite un NullPointer
        token = t == null ? "" : t;
        mRequestQueue = getRequestQueue(context);
        // TODO : Notifications ?
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(App.TAG, "response error \n" + error.networkResponse.statusCode);
            }
        };
        // Initialisation des Pending (Listes d'actions en attente)
        Pending.init(context);
    }

    /**
     * Initialisation du service de discussion avec le serveur d'APIs
     *
     * @param context Application
     * @param t   Token d'identification aux APIs
     */
    public static synchronized void init(Context context, String t) {
        if (instance != null)
            instance.setToken(t);
        else
            instance = new SyncFactory(context, t);
    }

    // Remarque : le mot clé "synchronized" permet de signifier que cette méthode ne peut pas être appelée deux fois en même temps
    // (Pendant son exécution un "verrou" permet d'empêcher une deuxième exécution)
    public static synchronized SyncFactory getInstance(Context context) {
        if (instance == null)
            init(context,null);
        return instance;
    }

    /**
     * Request Authentication Token
     *
     * @param lp    Instance de la page Login depuis laquelle est appelée la méthode
     * @param email Email de l'utilisateur
     * @param mdp   Mot de passe de l'utilisateur
     */
    public static void login(final LoginPage lp, String email, String mdp) {
        try {
            JSONObject cred = new JSONObject();
            // Données de la requete
            cred.put("email", email);
            cred.put("mdp", mdp);
            Log.i(App.TAG, cred.toString());
            // Preparation de la requete
            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST,
                    baseUrl + "login/",
                    cred,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(App.TAG, response.toString());
                            try {
                                if (response.has("token")) {
                                    lp.onLogin(response.getString("token"), response.getString("message"));
                                } else {
                                    lp.onErrorLogin(response.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO
                            error.printStackTrace();
                        }
                    }
            );
            // Execution de la requete
            getInstance(lp).getRequestQueue(lp).add(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getToken() {
        return token;
    }

    private void setToken(String t) {
        token = t;
    }

    /**
     * Récupère les devoirs depuis le serveur et les écrit au localStorage
     *
     * @param syncListener Instance à notifier lors de la récupération des devoirs
     * @param context      Android Context
     * @param version      Version requise des données
     */
    private void getWork(final SyncListener syncListener, final Context context, final String version) {
        Log.i(App.TAG, "test : GetWork ");
        req(context, "devoirs/" + (syncListener.isArchives() ? "?archives=1" : ""), Request.Method.GET, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Ecrire la réponse au localStorage (SharedPreferences)
                        if (syncListener.isArchives()) {
                            Work.setPastwork(context, response, version);
                        } else {
                            Work.setComingwork(context, response, version);
                        }
                        // Notification qu'une nouvelle version des données a été synchronisée
                        syncListener.onSync();
                    }
                }
        );
    }

    /**
     * Récupère la version distante des données (devoirs) et la compare avec la version locale
     *
     * @param syncListener Service à notifier en cas de changement de version
     * @param context      Android Context
     */
    public void getVersion(final SyncListener syncListener, final Context context) {
        Log.i(App.TAG, "test : GetVersion ");
        // Nom de la version à controler (Archives ou Devoirs)
        final String name = "version" + (syncListener.isArchives() ? "A" : "D");
        SharedPreferences preferences = context.getSharedPreferences(App.TAG, Context.MODE_PRIVATE);
        // Récupération de la version locale des données
        final String version = preferences.getString(name, "0");
        Log.i(App.TAG, "Version des données (" + (syncListener.isArchives() ? "A" : "D") + ") : " + version);
        // Requete de la version distante
        req(context, "version/", Request.Method.GET, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals(version)) {
                    // Si la version n'a pas changée, on notifie le SyncListener qui a appelé la mise à jour
                    syncListener.onSyncNotAvailable();
                } else {
                    // Une nouvelle version des données est disponible
                    Log.i(App.TAG, "Une nouvelle version des données est disponible : " + response);
                    getWork(syncListener, context, response);
                }
            }
        });
    }

    synchronized void synchronize(final SyncListener syncListener, final Context context, String json) {
        Log.i(App.TAG, "test : Synchronize ");
        // On commence par envoyer les actions en attente au serveur
        req(context, "pending/", Request.Method.POST, json, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // La liste de requêtes est bien parvenue au serveur
                // On nettoie la liste de requetes
                Pending.clear(context);
                // On récupère les nouveaux devoirs si nécessaire
                getVersion(syncListener, context);
            }
        });
    }

    private void req(Context context, String api, int method, final String postData, Response.Listener<String> listener) {
        // créé la requête
        Request req = new StringRequest(
                // GET, POST, PUT, DELETE...
                method,
                // URL d'accès à l'API
                baseUrl + api,
                // Response Listener (classe contenant une méthode de callback en cas de succes)
                listener,
                // Error Listener : Que faire en cas d'erreur
                errorListener
        ) {
            // Passage du token d'authentification en header de la requete HTTP
            // Définition du format de transfert des données (JSON)
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }

            // PostData
            @Override
            public byte[] getBody() throws AuthFailureError {
                return postData.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        // Ajout de la requête au "Thread HTTP"
        getRequestQueue(context).add(req);
    }

    /**
     * @param context Contexte depuis lequel sera récupéré l'ApplicationContext
     * @return File d'attente HTTP
     */
    private RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            // getApplicationContext() permet de faire une file d'attente commune à toute l'appli
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }
}
