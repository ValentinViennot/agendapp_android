package fr.agendapp.app.objects;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.SyncFactory;
import fr.agendapp.app.listeners.ClassicListener;

/**
 * Invitation à rejoindre un groupe
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Invite {

    private static List<Invite> invites = new LinkedList<>();
    /** ID dans la base */
    private int id;
    /** Prénom de l'utilisateur qui invite */
    private String de;
    /** Nom du groupe */
    private String groupe;
    /** ID du groupe invité */
    private int groupeid;

    /**
     * Constructeur par défaut
     * Utilisé par Gson. Les attributs sont initialisés par Gson.
     */
    public Invite() {
    }

    public static void setInvites(List<Invite> i) {
        invites = i;
        Log.i(App.TAG, "Nombre d'invitations : " + invites.size());
    }

    public int getId() {
        return id;
    }

    public int getGroupeid() {
        return groupeid;
    }

    public static class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.InviteHolder> {

        private Resources resources;

        @Override
        public InviteAdapter.InviteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            resources = parent.getContext().getResources();
            return new InviteAdapter.InviteHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(final InviteAdapter.InviteHolder holder, int position) {
            final Invite invite = invites.get(position);
            holder.text.setText(
                    resources.getString(
                            R.string.invit_text,
                            invite.de,
                            invite.groupe
                    ));
            holder.confirm.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SyncFactory.getInstance(v.getContext()).acceptInvite(
                                    v.getContext(),
                                    new ClassicListener() {
                                        @Override
                                        public void onCallBackListener() {
                                            // Après exécution de la requête
                                            deleteItem(holder.getAdapterPosition());
                                        }
                                    },
                                    invite,
                                    null // TODO Passage d'une instance de NotificationFactory
                            );
                        }
                    }
            );
            holder.cancel.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SyncFactory.getInstance(v.getContext()).declineInvite(
                                    v.getContext(),
                                    new ClassicListener() {
                                        @Override
                                        public void onCallBackListener() {
                                            // Après exécution de la requête
                                            deleteItem(holder.getAdapterPosition());
                                        }
                                    },
                                    invite,
                                    null // TODO
                            );
                        }
                    }
            );
        }

        @Override
        public int getItemCount() {
            return invites.size();
        }

        private void deleteItem(int position) {
            invites.remove(position);
            notifyItemRemoved(position);
        }

        class InviteHolder extends RecyclerView.ViewHolder {
            TextView text;
            Button confirm;
            Button cancel;

            InviteHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.object_invit, parent, false));
                this.text = (TextView) itemView.findViewById(R.id.invit_text);
                this.confirm = (Button) itemView.findViewById(R.id.invit_confirm);
                this.cancel = (Button) itemView.findViewById(R.id.invit_cancel);
            }

        }
    }
}