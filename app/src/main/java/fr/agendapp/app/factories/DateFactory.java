package fr.agendapp.app.factories;

import android.content.Context;
import android.content.res.Resources;

import java.util.Calendar;

import fr.agendapp.app.R;

/**
 * Service "Dates"
 * Fourni des resources pour le traitement des dates
 */
public final class DateFactory {

    /**
     * Remarque : L'utilisation des ressources permettra de traduire l'application en plusieurs langues
     *
     * @param month   Numéro du mois
     * @param context Android Context (Accès aux ressources)
     * @return nom du mois correspondant à la date
     */
    public static String getMonthName(Context context, int month) {
        if (context != null) {
            Resources r = context.getResources();
            switch (month) {
                case Calendar.JANUARY:
                    return r.getString(R.string.janvier);
                case Calendar.FEBRUARY:
                    return r.getString(R.string.fevrier);
                case Calendar.MARCH:
                    return r.getString(R.string.mars);
                case Calendar.APRIL:
                    return r.getString(R.string.avril);
                case Calendar.MAY:
                    return r.getString(R.string.mai);
                case Calendar.JUNE:
                    return r.getString(R.string.juin);
                case Calendar.JULY:
                    return r.getString(R.string.juillet);
                case Calendar.AUGUST:
                    return r.getString(R.string.aout);
                case Calendar.SEPTEMBER:
                    return r.getString(R.string.septembre);
                case Calendar.OCTOBER:
                    return r.getString(R.string.octobre);
                case Calendar.NOVEMBER:
                    return r.getString(R.string.novembre);
                case Calendar.DECEMBER:
                    return r.getString(R.string.decembre);
            }
        }
        return null;
    }

    /**
     * @param context Android
     * @param day     Jour de la semaine
     * @return représentation locale du jour de la semaine
     */
    public static String getWeekName(Context context, int day) {
        if (context != null) {
            Resources r = context.getResources();
            switch (day) {
                case Calendar.SUNDAY:
                    return r.getString(R.string.dimanche);
                case Calendar.MONDAY:
                    return r.getString(R.string.lundi);
                case Calendar.TUESDAY:
                    return r.getString(R.string.mardi);
                case Calendar.WEDNESDAY:
                    return r.getString(R.string.mercredi);
                case Calendar.THURSDAY:
                    return r.getString(R.string.jeudi);
                case Calendar.FRIDAY:
                    return r.getString(R.string.vendredi);
                case Calendar.SATURDAY:
                    return r.getString(R.string.samedi);
            }
        }
        return null;
    }

}
