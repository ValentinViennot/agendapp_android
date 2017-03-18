package fr.agendapp.app.factories;

import android.content.Context;
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
import fr.agendapp.app.pages.LoginPage;

public class SyncFactory {

    private static final String baseUrl = "https://apis.agendapp.fr/";
    private static SyncFactory instance = null;
    private String token;
    private RequestQueue mRequestQueue;
    private Response.ErrorListener errorListener;

    private SyncFactory(Context context, String token) {
        this.token = token == null ? "" : token;
        mRequestQueue = getRequestQueue(context);
        // TODO : Notifications ?
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(App.TAG, "response error \n" + error.networkResponse.statusCode);
            }
        };
    }

    /**
     * Initialisation du service de discussion avec le serveur d'APIs
     *
     * @param context Application
     * @param token   Token d'identification aux APIs
     */
    public static synchronized void init(Context context, String token) {
        if (instance != null)
            instance.setToken(token);
        else
            instance = new SyncFactory(context, token);
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

    private void setToken(String token) {
        this.token = token;
    }
}
