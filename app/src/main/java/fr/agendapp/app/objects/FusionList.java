package fr.agendapp.app.objects;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import fr.agendapp.app.R;
import fr.agendapp.app.utils.pending.PendMERGE;

public class FusionList {

    private TextView[] texts;
    private Work[] works;
    private CardView card;
    private Button confirm;
    private int size = 0;

    public FusionList(CardView card, TextView... texts) {
        this.texts = texts;
        this.card = card;
        this.confirm = (Button) card.findViewById(R.id.fusion_confirm);
        Button cancel = (Button) card.findViewById(R.id.fusion_cancel);
        cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clear();
                    }
                }
        );
        this.confirm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirm(v.getContext());
                    }
                }
        );
        clear();
    }

    public boolean add(Work w) {
        // Comparaison des dates
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        // Date du devoir à ajouter
        cal.setTime(w.getDate());
        for (int i = 0; i < works.length; i++) {
            // Si la case est vide, on peut y ajouter le devoir
            if (works[i] == null) {
                works[i] = w;
                // Défini le texte associé à la case de fusion sur le texte du devoir
                String t = (i + 1) + ") " + w.getText();
                texts[i].setText(t);
                // On rend visible le champ de texte
                texts[i].setVisibility(View.VISIBLE);
                // On rend visible la liste de fusion (car elle n'est plus vide - si elle l'était)
                card.setVisibility(View.VISIBLE);
                // Active le bouton de confirmation si au moins 2 devoirs à fusionner
                confirm.setActivated(true);
                // Operation réussie
                size++;
                return true;
            } else {
                // Si la case n'est pas vide, elle contient un devoir à fusionner
                // Vérifions si celui que l'on essaye d'ajouter est compatible avec
                cal2.setTime(works[i].getDate());
                if (// Si les deux devoirs à fusionner ne sont pas pour la meme date
                        cal.get(Calendar.DAY_OF_MONTH) != cal2.get(Calendar.DAY_OF_MONTH)
                                || cal.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)
                                // Ou qu'ils ne sont pas pour la même matière
                                || !w.getSubject().equals(works[i].getSubject())
                                // Ou qu'ils sont deux fois le même
                                || w.getId() == works[i].getId()
                        ) {
                    // Alors on refuse l'ajout à la liste de fusion
                    return false;
                }

            }
        }
        // Ajout impossible, taille de la liste de fusion dépassée
        return false;
    }

    private void clear() {
        // Masque la zone de fusion
        card.setVisibility(View.GONE);
        // Désactive le bouton de confirmation
        confirm.setActivated(false);
        // Réinitialise la liste de devoirs
        works = new Work[this.texts.length];
        size = 0;
        // Masque les champs de texte
        for (TextView t : texts)
            t.setVisibility(View.GONE);
    }

    private boolean confirm(Context context) {
        if (size > 0) {
            int[] merge = new int[size];
            for (int i = 0; i < size; ++i) {
                merge[i] = works[i].getId();
            }
            new PendMERGE(context, merge);
            clear();
        }
        // En cas d'échec
        return false;
    }

}
