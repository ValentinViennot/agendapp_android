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

class DoubleHeaderAdapter extends RecyclerView.Adapter<Work.ViewHolder> implements
        ca.barrenechea.widget.recyclerview.decoration.DoubleHeaderAdapter<DoubleHeaderAdapter.HeaderHolder, DoubleHeaderAdapter.SubHeaderHolder> {

    private List<Work> homeworks;
    private List<Header> headers;
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
        return getLongId(position, headers);
    }

    @Override
    public long getSubHeaderId(int position) {
        return getLongId(position, subheaders);
    }

    @Override
    public HeaderHolder onCreateHeaderHolder(ViewGroup parent) {
        return new HeaderHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public SubHeaderHolder onCreateSubHeaderHolder(ViewGroup parent) {
        return new SubHeaderHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindHeaderHolder(HeaderHolder viewholder, int position) {
        viewholder.title.setText(headers.get((int) getHeaderId(position)).getTitle());
    }

    @Override
    public void onBindSubHeaderHolder(SubHeaderHolder viewholder, int position) {
        viewholder.title.setText(subheaders.get((int) getSubHeaderId(position)).getTitle());
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.header, parent, false));
            title = (TextView) itemView.findViewById(R.id.headertitle);
        }
    }

    class SubHeaderHolder extends RecyclerView.ViewHolder {
        TextView title;

        SubHeaderHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.subheader, parent, false));
            title = (TextView) itemView.findViewById(R.id.subheadertitle);
        }
    }
}
