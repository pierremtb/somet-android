package io.somet.somet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.somet.somet.R;
import io.somet.somet.data.User;
import io.somet.somet.data.Workout;
import io.somet.somet.helpers.Tools;

public class MyAthletesAdapter extends
        RecyclerView.Adapter<MyAthletesAdapter.ViewHolder> {

    private List<User> myAthletes;

    public MyAthletesAdapter(List<User> myAthletes) {
        this.myAthletes = myAthletes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView athleteNames;

        public ViewHolder(View itemView) {
            super(itemView);

            athleteNames = (TextView) itemView.findViewById(R.id.athleteNames);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View workoutView = inflater.inflate(R.layout.item_my_athlete, parent, false);

        ViewHolder viewHolder = new ViewHolder(workoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        User athlete = myAthletes.get(position);

        TextView athleteNames = viewHolder.athleteNames;
        System.out.println(athlete);
        athleteNames.setText(athlete.getUsername());
    }

    @Override
    public int getItemCount() {
        return myAthletes.size();
    }
}