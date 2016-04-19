package io.somet.somet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Arrays;
import java.util.List;

import io.somet.somet.R;
import io.somet.somet.adapters.PlansAdapter;
import io.somet.somet.data.Plan;
import io.somet.somet.helpers.ItemClickSupport;

public class PlansActivity extends AppCompatActivity {

    String currentFieldSorting = "start_date";
    int currentSortingOrder = -1;

    int lastItemPosition = 0;

    RecyclerView rvItems;
    List<Plan> allPlans;
    PlansAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.nav_plans));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getResources().getString(R.string.notYetAvailable), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        rvItems = (RecyclerView) findViewById(R.id.rvPlans);

        lastItemPosition = 10;

        allPlans = Plan.createPlansList(-1, 0, currentFieldSorting, currentSortingOrder);

        adapter = new PlansAdapter(allPlans);
        rvItems.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvItems.setLayoutManager(linearLayoutManager);
        /*rvItems.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                System.out.println(page);
                List<Plan> morePlans = Plan.createPlansList(10, lastItemPosition, currentFieldSorting, currentSortingOrder);
                int curSize = adapter.getItemCount();
                allPlans.addAll(morePlans);
                adapter.notifyItemRangeInserted(curSize, allPlans.size() - 1);
                //adapter.notifyDataSetChanged();
                lastItemPosition += 10;
            }
        });*/

        ItemClickSupport.addTo(rvItems).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(getApplicationContext(), PlanActivity.class);
                Bundle b = new Bundle();
                b.putString("id", allPlans.get(position).getId());
                intent.putExtras(b);
                startActivityForResult(intent, 0);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plans, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sort) {
            int currentSortingFieldIndex = Arrays.asList(getResources().getStringArray(R.array.plans_sort_options_fields)).indexOf(currentFieldSorting);
            new MaterialDialog.Builder(this)
                    .title(R.string.action_sort_by)
                    .items(R.array.plans_sort_options)
                    .itemsCallbackSingleChoice(currentSortingFieldIndex, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            currentFieldSorting = getResources().getStringArray(R.array.plans_sort_options_fields)[which];
                            reloadPlanRecyclerView(-1,0);
                            return true;
                        }
                    })
                    .positiveText(R.string.choose)
                    .theme(com.afollestad.materialdialogs.Theme.LIGHT)
                    .show();
            return true;
        } else if (id == R.id.action_sort_direction) {
            String[] items = {getResources().getString(R.string.ascending), getResources().getString(R.string.descending)};
            new MaterialDialog.Builder(this)
                    .title(R.string.action_sort_direction)
                    .items(items)
                    .itemsCallbackSingleChoice((((-1)*currentSortingOrder)+1)/2, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            currentSortingOrder = (which*2-1) * -1;
                            reloadPlanRecyclerView(-1,0);
                            return true;
                        }
                    })
                    .positiveText(R.string.choose)
                    .theme(com.afollestad.materialdialogs.Theme.LIGHT)
                    .show();
            return true;
        } else {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void reloadPlanRecyclerView(int limit, int page) {
        List<Plan> morePlans = Plan.createPlansList(limit, page, currentFieldSorting, currentSortingOrder);
        int curSize = adapter.getItemCount();
        allPlans.clear();
        allPlans.addAll(morePlans);
        System.out.println(currentFieldSorting);
        adapter.notifyDataSetChanged();
    }

}
