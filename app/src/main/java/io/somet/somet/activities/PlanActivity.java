package io.somet.somet.activities;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.github.mikephil.charting.*;

import java.util.ArrayList;
import java.util.HashMap;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.SubscribeListener;
import io.somet.somet.R;
import io.somet.somet.helpers.Tools;
import io.somet.somet.data.Plan;

public class PlanActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    View view;

    public MaterialDialog loadingDialog;

    public Plan plan;
    public static String pl_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        pl_id = b.getString("id");
        plan = new Plan(MeteorSingleton.getInstance().getDatabase().getCollection("plans").getDocument(pl_id));

        setContentView(R.layout.activity_plan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(plan.getTitle());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), pl_id);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(mViewPager);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plan, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_totals) {
            new MaterialDialog.Builder(this)
                    .title(R.string.totals)
                    .content(String.format("Durée totale d'entrainement : %s", Tools.dispDuration(plan.getTotalDuration())))
                    .positiveText(R.string.close)
                    .theme(Theme.LIGHT)
                    .show();
        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    public static
    class OpenedPlanDayFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        private int day;
        private String pl_id;

        public TextView dayPlType, dayPlSupport, dayPlDuration, dayPlDescription;
        public CardView dayPlCard;
        public LinearLayout restView;

        public static OpenedPlanDayFragment newInstance(int day_i, String id) {
            OpenedPlanDayFragment fragmentFirst = new OpenedPlanDayFragment();
            Bundle args = new Bundle();
            args.putInt("day", day_i);
            args.putString("id", id);
            fragmentFirst.setArguments(args);
            return fragmentFirst;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.day = getArguments().getInt("day", 0);
            this.pl_id = getArguments().getString("id");
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_plan_day, container, false);

            Plan plan = new Plan(MeteorSingleton.getInstance().getDatabase().getCollection("plans").getDocument(pl_id));

            dayPlDescription = (TextView) rootView.findViewById(R.id.dayPlDescription);
            dayPlDuration = (TextView) rootView.findViewById(R.id.dayPlDuration);
            dayPlSupport = (TextView) rootView.findViewById(R.id.dayPlSupport);
            dayPlType = (TextView) rootView.findViewById(R.id.dayPlType);

            dayPlCard = (CardView) rootView.findViewById(R.id.dayPlCard);

            restView = (LinearLayout) rootView.findViewById(R.id.rest_view);

            System.out.println("auirestauie::::" + day);
            String type = plan.getDays().get(day).get("type");

            System.out.println(plan.getDays().get(day));

            if(Tools.dispType(type).equals(Tools.dispType("wk")) || Tools.dispType(type).equals(Tools.dispType("rc"))) {
                dayPlType.setText(Tools.dispType(type));
                dayPlSupport.setText(Tools.dispSupport(plan.getDays().get(day).get("support")));
                dayPlDescription.setText(plan.getDays().get(day).get("description"));
                if(Tools.dispType(type).equals(Tools.dispType("wk"))) {
                   dayPlDuration.setText(Tools.dispDuration(plan.getDays().get(day).get("duration")));
                }
            } else {
                dayPlCard.setVisibility(View.GONE);
                restView.setVisibility(View.VISIBLE);
            }

            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private String wk_id;

        public SectionsPagerAdapter(FragmentManager fm, String id) {
            super(fm);
            this.wk_id = id;
        }

        @Override
        public Fragment getItem(int position) {
            return OpenedPlanDayFragment.newInstance(position, wk_id);
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "LUNDI";
                case 1: return "MARDI";
                case 2: return "MERCREDI";
                case 3: return "JEUDI";
                case 4: return "VENDREDI";
                case 5: return "SAMEDI";
                case 6: return "DIMANCHE";
            }
            return null;
        }
    }

    public void setText(Integer id, Object txt) {
        try {
            ((TextView) getWindow().findViewById(id)).setText(txt.toString());
        } catch (Exception e ) { }
    }

    public void toast(String str) {
        Context context = getApplicationContext();
        CharSequence text = str;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


}
