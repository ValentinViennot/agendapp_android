package fr.agendapp.app.factories;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import fr.agendapp.app.listeners.AskListener;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Service de gestion des notifications et des fenetres de dialogue
 *
 * @author Valentin Viennot
 */
public class NotificationFactory {

    private Activity activity;

    public NotificationFactory(Activity activity) {
        this.activity = activity;
    }

    /**
     * Ajoute une 'notification' à l'écran de l'utilisateur
     *
     * @param activity Android Context
     * @param priority 0 : Info, 1 : Warn, 2 : Erreur (+ grand => + haute priorité)
     * @param titre    Titre du message
     * @param message  Texte du message à afficher
     */
    public static void add(Activity activity, int priority, String titre, String message) {
        if (priority > 1) {
            // Priorité importante => action de l'utilisateur pour valider la lecture
            final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();

            alertDialog.setTitle(titre);
            alertDialog.setMessage(message);
            alertDialog.setButton(BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();
        } else {
            // On affiche un Toast plus ou moins longtemps selon la priorité
            Toast.makeText(activity, titre + " - " + message, priority > 0 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Ajoute une fenetre de dialogue pour poser une question à l'utilisateur
     *
     * @param activity Android context
     * @param listener Callback
     * @param titre    Titre de la fenetre
     * @param message  Message de la fenetre de demande
     * @param ok       texte d'acceptation (bouton)
     * @param cancel   texte de refus (bouton)
     */
    public static void ask(Activity activity, final AskListener listener, String titre, String message, String ok, String cancel) {
        // Créé la fenetre de dialogue
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        // Titre et texte du message
        alertDialog.setTitle(titre);
        alertDialog.setMessage(message);
        // Bouton de validation (Ok) --> déclenche onAskOk()
        alertDialog.setButton(BUTTON_POSITIVE, ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onAskOk();
                alertDialog.dismiss();
            }
        });
        // Bouton de refus (cancel) --> déclenche onAskCancel()
        alertDialog.setButton(BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onAskCancel();
                alertDialog.dismiss();
            }
        });
        // Affiche la fenetre de dialogue
        alertDialog.show();
    }

    public void add(int priority, String title, String message) {
        add(activity, priority, title, message);
    }

}
