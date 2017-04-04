package fr.agendapp.app.objects;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import fr.agendapp.app.R;
import fr.agendapp.app.factories.SyncFactory;

/**
 * Pièce jointe attachée à un commentaire ou à un devoir
 *
 * @author Dylan Habans
 * @author Valentin Viennot
 */
public class Attachment {

    /**
     * "@prenomnom" de l'auteur de la pièce jointe
     */
    private String auteur;
    /**
     * ID de l'auteur
     */
    private int user;
    /**
     * Nom du fichier sur le serveur
     */
    private String file;
    /**
     * Nom lisible du fichier
     */
    private String title;

    /**
     * Constructeur par défaut, utilisé par GSON
     */
    public Attachment() {
    }

    /**
     * Lance le téléchargement de la pièce jointe sur l'appareil de l'utilisateur
     */
    private void download(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.getLink(SyncFactory.getToken())));
        context.startActivity(intent);
    }

    /**
     * Supprime la pièce jointe de la base de données
     */
    private void delete(Context context) {
        // La suppression nécessite une connexion à Internet
        SyncFactory.getInstance(context).deleteAttachment(context, this.getFile());
    }

    // GETTERS

    public int getUser() {
        return user;
    }

    public String getFile() {
        return file;
    }

    public String getTitle() {
        return title;
    }

    /**
     * @param token Token d'identification aux APIs
     * @return URL (lien) d'accès au fichier
     */
    private String getLink(String token) {
        return (
                "https://apis.agendapp.fr/cdn/?get=" + this.file + "&token=" + token
        );
    }

    /**
     * Adapter pour l'affichage d'une liste de piece jointe
     */
    public static class AttachmentAdapter extends BaseAdapter {

        private List<Attachment> attachments;
        private LayoutInflater inflater;

        public AttachmentAdapter(List<Attachment> attachments, LayoutInflater inflater) {
            this.attachments = attachments;
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return attachments.size();
        }

        @Override
        public Attachment getItem(int position) {
            return attachments.get(position);
        }

        @Override
        public long getItemId(int position) {
            // ignored
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            // Si la vue n'est pas recyclée
            if (convertView == null) {
                // On récupère le layout
                convertView = inflater.inflate(R.layout.object_attachment, parent, false);
                holder = new ViewHolder();
                // On place les widgets de notre layout dans le holder
                holder.title = (TextView) convertView.findViewById(R.id.attachment_title);
                holder.delete = (ImageButton) convertView.findViewById(R.id.attachment_delete);
                // puis on insère le holder en tant que tag dans le layout
                convertView.setTag(holder);
            } else {
                // Si on recycle la vue, on récupère son holder en tag
                holder = (ViewHolder) convertView.getTag();
            }

            final Attachment a = getItem(position);
            // Si cet élément existe vraiment…
            if (a != null) {
                holder.title.setText(a.getTitle());
                holder.title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        a.download(inflater.getContext());
                    }
                });
                if (User.getInstance().getId() == a.getUser()) {
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            attachments.remove(a);
                            notifyDataSetChanged();
                            a.delete(inflater.getContext());
                        }
                    });
                } else
                    holder.delete.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    /**
     * Vue associée à une piece jointe
     */
    private static class ViewHolder {
        TextView title;
        ImageButton delete;
    }

}