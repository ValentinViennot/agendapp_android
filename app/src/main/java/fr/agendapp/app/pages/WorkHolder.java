package fr.agendapp.app.pages;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import fr.agendapp.app.R;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.listeners.AskListener;
import fr.agendapp.app.objects.Attachment;
import fr.agendapp.app.objects.Work;

/**
 * Définition de l'affichage d'un devoir (UI)
 * Quels widgets sont nécessaires pour l'affichage ?
 * Comment sont affichées les données , réactions au clic etc
 */
class WorkHolder extends RecyclerView.ViewHolder {

    // Référence à l'adapter qui utilise cette vue
    private WorkAdapter adapter;
    // Référence à l'outil inflater
    private LayoutInflater inflater;

    private TextView subject;
    private TextView text;
    private ImageButton flag;
    private TextView nbDone;
    private ImageView imgDone;
    private TextView nbComm;
    private ImageView imgComm;
    private Button done;
    private ImageButton menu;

    WorkHolder(LayoutInflater inflater, ViewGroup parent, WorkAdapter adapter) {
        super(inflater.inflate(R.layout.object_work, parent, false));

        this.inflater = inflater;
        this.adapter = adapter;

        subject = (TextView) itemView.findViewById(R.id.card_subject);
        text = (TextView) itemView.findViewById(R.id.card_text);
        flag = (ImageButton) itemView.findViewById(R.id.card_flag);
        nbDone = (TextView) itemView.findViewById(R.id.card_nbDone);
        nbComm = (TextView) itemView.findViewById(R.id.card_nbComment);
        done = (Button) itemView.findViewById(R.id.button_done);//TODO marche pas après 1 synchro au premier clic (pas de MAJ affichage)
        menu = (ImageButton) itemView.findViewById(R.id.more_button);
        imgDone = (ImageView) itemView.findViewById(R.id.card_imgDone);
        imgComm = (ImageView) itemView.findViewById(R.id.card_imgComment);

    }

    /**
     * Méthode appelée à chaque fois qu'un devoir est affiché / actualisé
     * Code exécuté dans le Thread UI
     * (d'où la necessité de ne pas mettre à jour toute la liste à chaque MAJ)
     *
     * @param w Devoir
     */
    public void setWork(final Work w) {

        final Context context = inflater.getContext();
        final Resources r = context.getResources();

        // Matière
        subject.setText(w.getSubject());
        if (w.isDone())
            subject.setPaintFlags(subject.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else subject.setPaintFlags(text.getPaintFlags());
        subject.setTextColor(w.getSubjectColor());

        // Texte du devoir
        text.setText(Html.fromHtml(w.getText()));

        // Pièces jointes
        GridView gridview = (GridView) itemView.findViewById(R.id.card_attachments);
        gridview.setAdapter(new Attachment.AttachmentAdapter(w.getAttachments(), inflater));

        // Drapeau / Marqueur
        int color;
        switch (w.getFlag()) {
            case 1:
                // Bleu
                color = Color.parseColor("#4178BE");
                break;
            case 2:
                // Orange
                color = Color.parseColor("#FF7832");
                break;
            case 3:
                // Rouge
                color = Color.parseColor("#E71D32");
                break;
            default:
                // Gris
                color = Color.parseColor("#999999");
        }
        flag.setColorFilter(color);

        if (w.getId() > 0) {
            menu.setVisibility(View.VISIBLE);
            nbDone.setVisibility(View.VISIBLE);
            nbComm.setVisibility(View.VISIBLE);
            imgDone.setVisibility(View.VISIBLE);
            imgComm.setImageDrawable(r.getDrawable(R.drawable.ic_comment_black_24dp));

            // Sélection d'un marqueur
            flag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.flags_title)
                            .setItems(R.array.flags, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Modification locale + requete au serveur
                                    w.setFlag(context, which);
                                    // On signale à l'adapter qu'on vient de modifier la donnée locale
                                    adapter.notifyItemChanged(getAdapterPosition());
                                }
                            });
                    builder.create().show();
                }
            });

            // Menu
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, v);
                    final MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.work_menu, popup.getMenu());
                    MenuItem done = popup.getMenu().findItem(R.id.menu_done);
                    done.setTitle(w.isDone() ? R.string.button_undone : R.string.button_done);
                    MenuItem delete = popup.getMenu().findItem(R.id.menu_delete);
                    delete.setTitle(w.isUser() ? R.string.button_delete : R.string.button_alert);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_done:
                                    w.done(context);
                                    adapter.notifyItemChanged(getAdapterPosition());
                                    return true;
                                case R.id.menu_delete:
                                    if (w.isUser()) {
                                        NotificationFactory.ask(adapter.getActivity(), new AskListener() {
                                                    @Override
                                                    public void onAskOk() {
                                                        w.delete(context);
                                                        Work.notifyItemRemoved(getAdapterPosition(), w);
                                                        adapter.update();
                                                    }

                                                    @Override
                                                    public void onAskCancel() {
                                                        // Ne rien faire
                                                    }
                                                },
                                                "Confirmer la suppression ?",
                                                "Plus personne n'aura accès à ce devoir une fois supprimé",
                                                context.getResources().getString(R.string.button_confirm),
                                                context.getResources().getString(R.string.button_cancel));// TODO resources
                                    } else {
                                        w.report(context);
                                        NotificationFactory.add(adapter.getActivity(), 0, context.getResources().getString(R.string.msg_alert), "");
                                    }
                                    return true;
                                case R.id.menu_fusion:
                                    if (!adapter.fusion(w)) {
                                        NotificationFactory.add(adapter.getActivity(), 1, r.getString(R.string.msg_impossible), r.getString(R.string.msg_fusionimpossible));
                                    }
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });

            // Footer
            String nb = "" + w.getNbDone();
            nbDone.setText(nb);
            nb = "" + w.getComments().size();
            nbComm.setText(nb);

            if (w.isDone())
                done.setText(r.getString(R.string.button_undone));
            else
                done.setText(r.getString(R.string.button_done));

            // Boutons
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    w.done(context);
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });

            // Ouverture des commentaires
            View.OnClickListener openComm = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommentPage.setWork(w);
                    adapter.getActivity().startActivity(new Intent(adapter.getActivity(), CommentPage.class));
                }
            };
            nbDone.setOnClickListener(openComm);
            imgDone.setOnClickListener(openComm);
            nbComm.setOnClickListener(openComm);
            imgComm.setOnClickListener(openComm);
        } else {
            menu.setVisibility(View.INVISIBLE);
            nbDone.setVisibility(View.INVISIBLE);
            nbComm.setVisibility(View.INVISIBLE);
            imgDone.setVisibility(View.INVISIBLE);

            done.setOnClickListener(null);
            imgComm.setOnClickListener(null);
            imgComm.setImageDrawable(r.getDrawable(R.drawable.ic_cached_black_24dp));//TODO sync pic
            // TODO resources
            done.setText("Envoi en cours..");
        }
    }
}