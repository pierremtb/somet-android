package io.somet.somet.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.somet.somet.R;
import io.somet.somet.data.Plan;
import io.somet.somet.helpers.Tools;

public class PlansAdapter extends
        RecyclerView.Adapter<PlansAdapter.ViewHolder> {

    private List<Plan> plans;

    public PlansAdapter(List<Plan> plans) {
        this.plans = plans;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView planTitle, planDate;

        public ViewHolder(View itemView) {
            super(itemView);

            planTitle = (TextView) itemView.findViewById(R.id.planTitle);
            planDate = (TextView) itemView.findViewById(R.id.planDate);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View planView = inflater.inflate(R.layout.item_plan, parent, false);

        ViewHolder viewHolder = new ViewHolder(planView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Plan plan = plans.get(position);

        TextView planTitle = viewHolder.planTitle;
        planTitle.setText(plan.getTitle());

        TextView planDate = viewHolder.planDate;
        planDate.setText(String.format("%s - %s", Tools.dispDate(plan.getMondayDate()),Tools.dispDuration(plan.getTotalDuration())));
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }
}
