package fr.agendapp.app.pages;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;

import fr.agendapp.app.R;
import fr.agendapp.app.objects.Attachment;
import fr.agendapp.app.objects.Header;
import fr.agendapp.app.objects.Work;

/**
 * Adapteur pour l'affichage de la liste de devoirs
 */
class DoubleHeaderAdapter extends RecyclerView.Adapter<DoubleHeaderAdapter.ViewHolder> implements
        ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderAdapter<DoubleHeaderAdapter.HeaderHolder, DoubleHeaderAdapter.SubHeaderHolder> {

    // Liste de devoirs utilisée par l'adapter
    private List<Work> homeworks;
    // Liste de headers utilisée par l'adapter
    private List<Header> headers;
    // Liste d'en tetes de jour utilisée par l'adapter
    private List<Header> subheaders;

    DoubleHeaderAdapter(WorkPage wp) {
        this.homeworks = wp.getHomeworks();
        this.headers = wp.getHeaders();
        this.subheaders = wp.getSubheaders();
    }

    void updateList(WorkPage wp) {
        // TODO utiliser la méthode d'update de this.homeworks seulement où il y a eu des changements
        // TODO exécuter cette méthode dans un thread
        this.homeworks = wp.getHomeworks();
        this.headers = wp.getHeaders();
        this.subheaders = wp.getSubheaders();
        this.notifyDataSetChanged();
    }

    @Override
    public DoubleHeaderAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Création d'un modèle de Vue pour les en tetes de mois
        return new DoubleHeaderAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
    }

    @Override
    public void onBindViewHolder(DoubleHeaderAdapter.ViewHolder holder, int position) {
        holder.setWork(homeworks.get(position));
    }

    @Override
    public int getItemCount() {
        return homeworks.size();
    }

    private long getLongId(int position, List<Header> headers) {
        int i = headers.size(), total;
        ListIterator<Header> li = headers.listIterator(i);
        // Iteration dans le sens inversé
        while (li.hasPrevious()) {
            i--;
            total = li.previous().getTo();
            if (position >= total) {
                return i + position / total;
            }
        }
        return 0;
    }

    @Override
    public long getHeaderId(int position) {
        // Retourne la position de l'en tete de mois associé au devoir à cette position de la liste
        return getLongId(position, headers);
    }

    @Override
    public long getSubHeaderId(int position) {
        // Retourne la position de l'en tete de jour associé au devoir à cette position de la liste
        return getLongId(position, subheaders);
    }

    @Override
    public HeaderHolder onCreateHeaderHolder(ViewGroup parent) {
        // Création d'un modèle de Vue pour les en tetes de mois
        return new HeaderHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public SubHeaderHolder onCreateSubHeaderHolder(ViewGroup parent) {
        // Création d'un modèle de Vue pour les en tetes de jour
        return new SubHeaderHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindHeaderHolder(HeaderHolder viewholder, int position) {
        // Mise à jour de la vue de l'en tete de mois associé au devoir à cette position
        viewholder.title.setText(headers.get((int) getHeaderId(position)).getTitle());
    }

    @Override
    public void onBindSubHeaderHolder(SubHeaderHolder viewholder, int position) {
        // Mise à jour de la vue de l'en tete de jour associé au devoir à cette position
        viewholder.title.setText(subheaders.get((int) getSubHeaderId(position)).getTitle());
    }

    /**
     * Vue pour un en tete de mois
     */
    class HeaderHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.header, parent, false));
            title = (TextView) itemView.findViewById(R.id.headertitle);
        }
    }

    /**
     * Vue pour un en tete de jour
     */
    class SubHeaderHolder extends RecyclerView.ViewHolder {
        TextView title;

        SubHeaderHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.subheader, parent, false));
            title = (TextView) itemView.findViewById(R.id.subheadertitle);
        }
    }

    /**
     * Définition de l'affichage d'un devoir (UI)
     * Quels widgets sont nécessaires pour l'affichage ?
     * Comment sont affichées les données , réactions au clic etc
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        LayoutInflater inflater;

        private CardView card;
        private RelativeLayout cardHeader;
        private TextView subject;
        private TextView text;
        private ImageButton flag;
        private TextView nbDone;
        private TextView nbComm;
        private Button done;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.object_work, parent, false));
            this.inflater = inflater;

            card = (CardView) itemView.findViewById(R.id.card_view);
            cardHeader = (RelativeLayout) itemView.findViewById(R.id.card_header);
            subject = (TextView) itemView.findViewById(R.id.card_subject);
            text = (TextView) itemView.findViewById(R.id.card_text);
            flag = (ImageButton) itemView.findViewById(R.id.card_flag);
            nbDone = (TextView) itemView.findViewById(R.id.card_nbDone);
            nbComm = (TextView) itemView.findViewById(R.id.card_nbComment);
            done = (Button) itemView.findViewById(R.id.button_done);

        }

        public void setWork(final Work w) {

            // Matière
            subject.setText(w.getSubject());
//            subject.setTextColor(w.getSubjectColor());
            cardHeader.setBackgroundColor(w.getSubjectColor());

            // Texte du devoir
            text.setText(w.getText());

            // Drapeau / Marqueur
            int color;
            switch (w.getFlag()) {
                case 1:
                    color = Color.parseColor("#4178BE");
                    break;
                case 2:
                    color = Color.parseColor("#FF7832");
                    break;
                case 3:
                    color = Color.parseColor("#E71D32");
                    break;
                default:
                    color = Color.parseColor("#999999");
            }
            flag.setColorFilter(color);

            // Pièces jointes
            GridView gridview = (GridView) itemView.findViewById(R.id.card_attachments);
            gridview.setAdapter(new Attachment.AttachmentAdapter(w.getAttachments(), inflater));

            // Footer
            String nb = "" + w.getNbDone();
            nbDone.setText(nb);
            nb = "" + w.getComments().size();
            nbComm.setText(nb);

            // Boutons
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    w.done(inflater.getContext());
                    notifyItemChanged(getAdapterPosition());
                }
            });

        }
    }
}
