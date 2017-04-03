package fr.agendapp.app.utils.filters;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import fr.agendapp.app.R;
import fr.agendapp.app.objects.Subject;
import fr.agendapp.app.objects.User;
import fr.agendapp.app.pages.FilterInterface;

public class SelectableFilter implements Comparable<SelectableFilter> {

    private Filter filter;
    private int count;

    SelectableFilter(Filter filter) {
        this.filter = filter;
        this.count = 0;
    }

    void inc() {
        count++;
    }

    public Filter getFilter() {
        return this.filter;
    }

    @Override
    public int compareTo(@NonNull SelectableFilter o) {
        return o.count - this.count;
    }

    public static class FilterAdapter extends RecyclerView.Adapter<FilterHolder> {

        List<Filter> filters;
        FilterInterface parent;

        public FilterAdapter(FilterInterface parent) {
            this.filters = new LinkedList<>();
            this.parent = parent;
        }

        public void setSelectableFilters(List<SelectableFilter> filters) {
            for (SelectableFilter filter : filters)
                this.filters.add(filter.getFilter());
            this.notifyDataSetChanged();
        }

        public void setFilters(List<Filter> filters) {
            this.filters = filters;
            this.notifyDataSetChanged();
        }

        @Override
        public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FilterHolder(LayoutInflater.from(parent.getContext()), parent, this);
        }

        @Override
        public void onBindViewHolder(FilterHolder holder, int position) {
            holder.setFilter(filters.get(position));
        }

        @Override
        public int getItemCount() {
            return filters.size();
        }
    }

    static class FilterHolder extends RecyclerView.ViewHolder {

        private LayoutInflater inflater;
        private FilterAdapter adapter;

        private TextView text;
        private RelativeLayout layout;
        private ImageView image;

        FilterHolder(LayoutInflater inflater, ViewGroup parent, FilterAdapter adapter) {
            super(inflater.inflate(R.layout.object_filter, parent, false));
            this.inflater = inflater;
            this.adapter = adapter;
            text = (TextView) itemView.findViewById(R.id.textfilter);
            layout = (RelativeLayout) itemView.findViewById(R.id.filterlayout);
            layout.setClickable(true);
            image = (ImageView) itemView.findViewById(R.id.filtercolor);
        }

        @SuppressWarnings("deprecation")
        void setFilter(final Filter filter) {
            Resources r = inflater.getContext().getResources();
            image.setImageDrawable(r.getDrawable(R.drawable.circle));
            layout.setScaleX(1f);
            layout.setScaleY(1f);
            int color = Color.parseColor("#999999");
            if (filter instanceof FilterSubject) {
                text.setText(((FilterSubject) filter).getSubject());
                for (Subject s : User.getInstance().getSubjects())
                    if (s.getNom().equals(((FilterSubject) filter).getSubject())) {
                        color = s.getColor();
                        break;
                    }
            } else if (filter instanceof FilterFlag) {
                text.setText("");
                image.setImageDrawable(r.getDrawable(R.drawable.ic_flag_black_24dp));
                layout.setScaleX(0.5f);
                layout.setScaleY(0.5f);
                switch (((FilterFlag) (filter)).getFlag()) {
                    case 1:
                        // Bleu
                        color = r.getColor(R.color.flag_blue);
                        break;
                    case 2:
                        // Orange
                        color = r.getColor(R.color.flag_orange);
                        break;
                    case 3:
                        // Rouge
                        color = r.getColor(R.color.flag_red);
                        break;
                    default:
                        // Gris
                        color = r.getColor(R.color.flag_grey);
                }
            } else {
                text.setText(filter.toString());
            }

            image.setColorFilter(color);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (filter.isActive())
                        Filter.clearFilter(filter);
                    else
                        Filter.addFilter(filter);
                    adapter.parent.applyFilters();
                }
            });
        }
    }
}
