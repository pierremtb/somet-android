package io.somet.somet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.somet.somet.R;
import io.somet.somet.data.Event;
import io.somet.somet.helpers.Tools;

public class EventsAdapter extends
        RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<Event> events;

    public EventsAdapter(List<Event> events) {
        this.events = events;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView eventTitle, eventDate;

        public ViewHolder(View itemView) {
            super(itemView);

            eventTitle = (TextView) itemView.findViewById(R.id.eventTitle);
            eventDate = (TextView) itemView.findViewById(R.id.eventDate);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.item_event, parent, false);

        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Event event = events.get(position);

        TextView eventTitle = viewHolder.eventTitle;
        eventTitle.setText(event.getTitle());

        TextView eventDate = viewHolder.eventDate;
        eventDate.setText(Tools.dispDate(event.getDate()));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
