package io.somet.somet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.somet.somet.R;
import io.somet.somet.data.Workout;
import io.somet.somet.helpers.Tools;

public class WorkoutsAdapter extends
        RecyclerView.Adapter<WorkoutsAdapter.ViewHolder> {

    private List<Workout> workouts;

    public WorkoutsAdapter(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView workoutTitle, workoutDate;

        public ViewHolder(View itemView) {
            super(itemView);

            workoutTitle = (TextView) itemView.findViewById(R.id.workoutTitle);
            workoutDate = (TextView) itemView.findViewById(R.id.workoutDate);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View workoutView = inflater.inflate(R.layout.item_workout, parent, false);

        ViewHolder viewHolder = new ViewHolder(workoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Workout workout = workouts.get(position);

        TextView workoutTitle = viewHolder.workoutTitle;
        workoutTitle.setText(workout.getTitle());

        TextView workoutDate = viewHolder.workoutDate;
        workoutDate.setText(String.format("%s - %s - %.1fkm", Tools.dispDate(workout.getStartDate()),Tools.dispDuration(workout.getDuration()), workout.getDistance()/1000));
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }
}
