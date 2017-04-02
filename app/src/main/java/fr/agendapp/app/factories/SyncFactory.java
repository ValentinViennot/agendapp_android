package fr.agendapp.app.factories;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import fr.agendapp.app.R;
import fr.agendapp.app.listeners.ClassicListener;
import fr.agendapp.app.listeners.SyncListener;
import fr.agendapp.app.objects.Invite;
import fr.agendapp.app.objects.User;
import fr.agendapp.app.objects.Work;
import fr.agendapp.app.pages.LoginPage;
import fr.agendapp.app.pending.Pending;

public class SyncFactory {

    private static final String baseUrl = "https://apis.agendapp.fr/";
    private static boolean offline = false;
    private static String servererror = null;
    /**
     * Instance active du service de synchronisation
     */
    private static SyncFactory instance = null;
    /**
     * Token d'identification aux APIs
     */
    private static String token;
    /**
     * File d'attente des requêtes HTTP (Pile du Thread HTTP)
     */
    private RequestQueue mRequestQueue;
    /**
     * True si la liste de pending est en cours d'envoi
     */
    private boolean lockpending = false;
    /**
     * Création d'une nouvelle instance du service de synchronisation
     *
     * @param context Android
     * @param t       Token API
     */
    private SyncFactory(Context context, String t) {
        // Evite un NullPointer
        token = t == null ? "" : t;
        // Initialisation de la liste d'attente
        mRequestQueue = getRequestQueue(context);
        // Initialisation des Pending (Listes d'actions en attente)
        Pending.init(context);
    }

    public static boolean isOffline() {
        return offline;
    }

    public static String getServererror() {
        return servererror;
    }

    /**
     * Initialisation du service de discussion avec le serveur d'APIs
     *
     * @param context APPLICATION Context
     * @param t       Token d'identification aux APIs
     */
    public static synchronized void init(Context context, String t) {
        // Initialisation des lites d'actions en attente
        Pending.init(context);
        if (instance != null)
            instance.setToken(t);
        else
            instance = new SyncFactory(context, t);
    }

    // Remarque : le mot clé "synchronized" permet de signifier que cette méthode ne peut pas être appelée deux fois en même temps
    // (Pendant son exécution un "verrou" permet d'empêcher une deuxième exécution)

    /**
     * Récupère l'instance active du service de synchronisation.
     * En cas de non existence, on en créé une sans token (donc nécessite identification)
     *
     * @param context APPLICATION Context
     * @return SyncFactory active instance
     */
    public static synchronized SyncFactory getInstance(Context context) {
        if (instance == null)
            init(context, null);
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
                            checkServerStatus(lp, null);
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

    public static void checkServerStatus(final Context context, @Nullable final ClassicListener classicListener) {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                baseUrl + "status/",
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("message")) {
                                servererror = response.getString("message");
                            } else {
                                servererror = null;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            servererror = context.getResources().getString(R.string.code_0_message);
                        } finally {
                            offline = false;
                            if (classicListener != null)
                                classicListener.onCallBackListener();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        servererror = null;
                        offline = true;
                        if (classicListener != null)
                            classicListener.onCallBackListener();
                    }
                }
        );
        // Execution de la requete
        getInstance(context).getRequestQueue(context).add(req);
    }

    public void getUser(final Context context, final ClassicListener classicListener, @Nullable NotificationFactory notifs) {
        req(context, "user/", Request.Method.GET, "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Une fois l'utilisateur récupéré
                        // On met à jour l'utilisateur actif avec les données récupérées du serveur
                        SharedPreferences preferences = context.getSharedPreferences(App.TAG, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("user", response);
                        editor.apply();
                        // On actualise l'instance de User
                        User.init(context);
                        // On appelle la méthode de callback passée en paramètre
                        classicListener.onCallBackListener();
                    }
                }
                , notifs);
    }

    public void saveUser(final Context context, final ClassicListener classicListener, final NotificationFactory notifs, String json) {
        req(context, "user/", Request.Method.POST, json,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getUser(context, classicListener, notifs);
                    }
                }
                , notifs);
    }

    /**
     * Déconnexion de l'utilisateur (sécurité) = effacement du token
     */
    public void logout(Context context) {
        req(context, "logout/", Request.Method.GET, "", null, (NotificationFactory) null);
    }

    public void deleteAttachment(Context context, final String id) {
        req(context, "cdn/?id=" + id + "&delete", Request.Method.GET, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(App.TAG, "Attchment " + id + " deleted from server");
            }
        }, (NotificationFactory) null);
    }

    public void acceptInvite(Context context, final ClassicListener classicListener, Invite invite, final NotificationFactory notifs) {
        req(context, "invitations/?id=" + invite.getId(), Request.Method.POST,
                "{\"groupe\":" + invite.getGroupeid() + "}",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        classicListener.onCallBackListener();
                        if (notifs != null) notifs.add(0, "Invitation acceptée", "");
                    }
                }, notifs);
    }

    public void declineInvite(Context context, final ClassicListener classicListener, Invite invite, final NotificationFactory notifs) {
        req(context, "invitations/?id=" + invite.getId(), Request.Method.DELETE, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                classicListener.onCallBackListener();
                if (notifs != null) notifs.add(0, "Invitation refusée", "");
            }
        }, notifs);
    }

    public void getInvites(Context context, final ClassicListener classicListener) {
        // Récupère les invitations depuis le serveur
        req(context, "invitations/", Request.Method.GET, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Récupère la liste d'invitations depuis le format JSON
                Invite.setInvites(ParseFactory.parseInvites(response));
                // Appel du callback
                classicListener.onCallBackListener();
            }
        }, (NotificationFactory) null);
    }

    /**
     * Récupère les devoirs depuis le serveur et les écrit au localStorage
     *
     * @param syncListener Instance à notifier lors de la récupération des devoirs
     * @param context      Android Context
     * @param version      Version requise des données
     */
    private void getWork(final SyncListener syncListener, final Context context, final String version, NotificationFactory notifs) {
        if (context != null) {
            req(context, "devoirs/" + (syncListener.isArchives() ? "?archives=1" : ""), Request.Method.GET, "",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(final String response) {
                            // L'operation setXXXwork risque d'etre longue, on prefere donc l'executer dans un Thread separe
                            // Seulement une fois que tout est terminé, on previent le callback
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    // Ecrire la réponse au localStorage (SharedPreferences)
                                    if (syncListener.isArchives()) {
                                        Work.setPastwork(context, response, version);
                                    } else {
                                        Work.setComingwork(context, response, version);
                                    }
                                    // Notification qu'une nouvelle version des données a été synchronisée
                                    syncListener.onSync();
                                }
                            }.run();
                        }
                    }, notifs
            );
        }
    }

    /**
     * Récupère la version distante des données (devoirs) et la compare avec la version locale
     *
     * @param syncListener Service à notifier en cas de changement de version
     * @param context      Android Context
     */
    public void getVersion(final SyncListener syncListener, final Context context, final NotificationFactory notifs) {
        if (context != null && syncListener != null) {
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
                        getWork(syncListener, context, response, notifs);
                    }
                }
            }, notifs);
        }
    }

    /**
     * Le mot clé 'synchronized' permet d'éviter que cette méthode soit exécutée deux fois simultanément, et donc d'envoyer
     * deux fois la même liste d'actions au serveur.
     * Comme cette méthode met en réalité une requête en attente elle est exécutée rapidement et ce n'est pas suffisant
     * C'est pourquoi on passe par un attribut booleen pour tester si un envoi est en cours via cette instance de SyncFactory
     *
     * @param syncListener Callback
     * @param context      Android Context
     * @param json         Liste de requêtes à envoyer au format JSON
     * @param notifs       Pour ajouter des notifications en cas d'erreur @Nullable
     */
    public synchronized void synchronize(final SyncListener syncListener, final Context context, String json, @Nullable final NotificationFactory notifs) {
        if (context != null && syncListener != null) {
            // Si un envoi n'est pas déjà en cours
            if (!lockpending) {
                // On bloque l'envoi de listes d'actions
                setLockpending(true);
                // On commence par envoyer les actions en attente au serveur
                req(context, "pending/", Request.Method.POST, json, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // La liste de requêtes est bien parvenue au serveur
                        // On nettoie la liste locale de requetes
                        Pending.clear(context);
                        // On débloque l'envoi de listes d'actions
                        setLockpending(false);
                        // On récupère les nouveaux devoirs si nécessaire (si des actions viennent d'être effectuées, cela le sera)
                        getVersion(syncListener, context, notifs);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // En cas d'erreur d'envoi de la liste de requête
                        // On débloque l'envoi de listes d'actions
                        setLockpending(false);
                        // On fait appel à la gestion d'erreur par défaut de SyncFactory
                        getErrorListener(notifs).onErrorResponse(error);
                    }
                });
            } else {
                syncListener.onSyncNotAvailable();
            }
        }
    }

    private void req(Context context, String api, int method, final String postData, Response.Listener<String> listener, @NonNull Response.ErrorListener errorListener) {
        if (context != null) {
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
    }

    /**
     * Ajoute une requête avec le ErrorListener par défaut
     */
    private void req(Context context, String api, int method, final String postData, Response.Listener<String> listener, @Nullable NotificationFactory notifs) {
        req(context, api, method, postData, listener, getErrorListener(notifs));
    }

    private Response.ErrorListener getErrorListener(final NotificationFactory notifs) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    switch (error.networkResponse.statusCode) {
                        case 400:
                            if (notifs != null)
                                notifs.add(2, R.string.code_400_title, R.string.code_400_message);
                        case 404:
                            if (notifs != null)
                                notifs.add(2, R.string.code_404_title, R.string.code_404_message);
                            Log.i(App.TAG, "Http 404");
                            break;
                        case 401:
                            if (notifs != null) {
                                // Efface les données locales de l'utilisateur
                                SharedPreferences preferences = notifs.getActivity().getSharedPreferences(App.TAG, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();
                                // Ajoute une notification explicative
                                notifs.add(1, "Identification impossible", "Reconnecte toi...");
                                // Renvoyer l'utilisateur sur la page d'identification
                                notifs.getActivity().startActivity(new Intent(notifs.getActivity(), App.class));
                            }
                            break;
                        case 503:
                            if (notifs != null)
                                notifs.add(2, R.string.code_503_title, R.string.code_503_message);
                        default:
                            if (notifs != null)
                                notifs.add(1, R.string.code_0_title, R.string.code_0_message);
                            Log.w(App.TAG, "Code HTTP non géré : " + error.networkResponse.statusCode);
                    }
                } else {
                    // On teste la connexion au serveur grâce à une API spéciale
                    Log.i(App.TAG, "Test de connexion au serveur...");
                    if (notifs != null) checkServerStatus(notifs.getActivity(), null);
                }
            }
        };
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

    private void setLockpending(boolean b) {
        lockpending = b;
    }
}
