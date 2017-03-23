package fr.agendapp.app.pages;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;

import fr.agendapp.app.R;
import fr.agendapp.app.objects.Header;
import fr.agendapp.app.objects.Work;

/**
 * Adapteur pour l'affichage de la liste de devoirs
 */
class DoubleHeaderAdapter extends RecyclerView.Adapter<Work.ViewHolder> implements
        ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderAdapter<DoubleHeaderAdapter.HeaderHolder, DoubleHeaderAdapter.SubHeaderHolder> {

    // Liste de devoirs utilisée par l'adapter
    private List<Work> homeworks;
    // Liste de headers utilisée par l'adapter
    private List<Header> headers;
    // Liste d'en tetes de jour utilisée par l'adapter
    private List<Header> subheaders;

    DoubleHeaderAdapter(WorkPage wp) {
        updateList(wp);
    }

    void updateList(WorkPage wp) {
        this.homeworks = wp.getHomeworks();
        this.headers = wp.getHeaders();
        this.subheaders = wp.getSubheaders();
        this.notifyDataSetChanged();
    }

    @Override
    public Work.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Création d'un modèle de Vue pour les en tetes de mois
        return new Work.ViewHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
    }

    @Override
    public void onBindViewHolder(Work.ViewHolder holder, int position) {
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
}
