package io.somet.somet;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class WorkoutsAdapter extends
        RecyclerView.Adapter<WorkoutsAdapter.ViewHolder> {

    // Store a member variable for the workouts
    private List<Workout> workouts;

    // Pass in the workout array into the constructor
    public WorkoutsAdapter(List<Workout> workouts) {
        this.workouts = workouts;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            //nameTextView = (TextView) itemView.findViewById(R.id.workout_name);
            //messageButton = (Button) itemView.findViewById(R.id.message_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View workoutView = inflater.inflate(R.layout.content_main, parent, false);

        ViewHolder viewHolder = new ViewHolder(workoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Workout workout = workouts.get(position);

        TextView textView = viewHolder.nameTextView;
        textView.setText(workout.getTitle());

        Button button = viewHolder.messageButton;
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }
}
