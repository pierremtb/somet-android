package io.somet.somet.activities;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
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

import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import io.somet.somet.data.Workout;

public class WorkoutActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    View view;

    public MaterialDialog loadingDialog;

    public Workout workout;
    public static String wk_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        wk_id = b.getString("id");
        workout = new Workout(MeteorSingleton.getInstance().getDatabase().getCollection("workouts").getDocument(wk_id));
        System.out.println(workout);

        setContentView(R.layout.activity_workout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(workout.getTitle());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
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
        getMenuInflater().inflate(R.menu.menu_workout, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    public static class OpenedWorkoutDetailsFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        static Workout workout;
        public TextView openedWkComments;
        public TextView openedWkDescription;
        public TextView openedWkDuration;
        public TextView openedWkDate;

        public OpenedWorkoutDetailsFragment() {
        }

        public static OpenedWorkoutDetailsFragment newInstance(Workout wk) {
            OpenedWorkoutDetailsFragment fragment = new OpenedWorkoutDetailsFragment();
            workout = wk;
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_workout_details, container, false);

            openedWkDescription = (TextView) rootView.findViewById(R.id.openedWkDescription);
            openedWkComments = (TextView) rootView.findViewById(R.id.openedWkComments);
            openedWkDate = (TextView) rootView.findViewById(R.id.openedWkDate);
            openedWkDuration = (TextView) rootView.findViewById(R.id.openedWkDuration);

            openedWkDescription.setText(workout.getDescription());
            openedWkComments.setText(workout.getComments());
            openedWkDate.setText(Tools.dispDate(workout.getStartDate()));
            openedWkDuration.setText(Tools.dispDuration(workout.getDuration()));

            return rootView;
        }
    }

    public static class OpenedWorkoutCRsFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        static Workout workout;

        public OpenedWorkoutCRsFragment() {
        }

        public static OpenedWorkoutCRsFragment newInstance(Workout wk) {
            OpenedWorkoutCRsFragment fragment = new OpenedWorkoutCRsFragment();
            workout = wk;
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_workout_crs, container, false);

            drawCircle(R.id.arcCREffort, rootView, workout.getCrEffort());
            drawCircle(R.id.arcCRPleasure, rootView, workout.getCrPleasure());
            drawCircle(R.id.arcCRSensations, rootView, workout.getCrSensations());
            drawCircle(R.id.arcCRMood, rootView, workout.getCrMood());

            return rootView;
        }

        public void drawCircle(Integer id, View container, float value) {
            DecoView arcView1 = (DecoView) container.findViewById(id);

            arcView1.addSeries(new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                    .setRange(0, 10, 10)
                    .setInitialVisibility(false)
                    .setLineWidth(32f)
                    .build());

            SeriesItem seriesItem1 = new SeriesItem.Builder(getResources().getColor(R.color.colorAccent))
                    .setRange(0, 10, 0)
                    .setLineWidth(32f)
                    .build();

            int series1Index = arcView1.addSeries(seriesItem1);

            arcView1.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                    .setDelay(0)
                    .setDuration(100)
                    .build());

            arcView1.addEvent(new DecoEvent.Builder(value).setIndex(series1Index).setDelay(0).setDuration(200).build());
        }

    }

    public static class OpenedWorkoutGraphsFragment extends Fragment {
        static Workout workout;

        public OpenedWorkoutGraphsFragment() {
        }

        public static OpenedWorkoutGraphsFragment newInstance(Workout wk) {
            OpenedWorkoutGraphsFragment fragment = new OpenedWorkoutGraphsFragment();
            workout = wk;
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_workout_graphs, container, false);

            final LineChart lineChartCadence = (LineChart) rootView.findViewById(R.id.chartCadence);
            final LineChart lineChartElevation = (LineChart) rootView.findViewById(R.id.chartElevation);
            final LineChart lineChartSpeed = (LineChart) rootView.findViewById(R.id.chartSpeed);
            final LineChart lineChartHeartRate = (LineChart) rootView.findViewById(R.id.chartHeartRate);
            final LineChart lineChartPower = (LineChart) rootView.findViewById(R.id.chartPower);

            MeteorSingleton.getInstance().subscribe("workoutOfThisIdSync", new Object[]{wk_id}, new SubscribeListener() {
                @Override
                public void onSuccess() {
                    ArrayList<Entry> entriesCadence = new ArrayList<>();
                    ArrayList<Entry> entriesElevation = new ArrayList<>();
                    ArrayList<Entry> entriesSpeed = new ArrayList<>();
                    ArrayList<Entry> entriesHeartRate = new ArrayList<>();
                    ArrayList<Entry> entriesPower = new ArrayList<>();

                    ArrayList<String> labels = new ArrayList<>();

                    for (int i = 0; i < workout.getFitTimeValues().length; i++) {
                        if (i % 10 == 0) {
                            Integer index = (int)(long) workout.getFitTimeValues()[i];
                            Integer cad = workout.getFitCadenceValues()[i];
                            Integer hr = workout.getFitHeartRateValues()[i];
                            Integer elev = (int) workout.getFitElevationValues()[i];
                            Integer sp = (int) workout.getFitSpeedValues()[i];
                            Integer pw = workout.getFitPowerValues()[i];

                            entriesCadence.add(new Entry(cad, index));
                            entriesElevation.add(new Entry(elev, index));
                            entriesSpeed.add(new Entry(sp, index));
                            entriesPower.add(new Entry(pw, index));
                            entriesHeartRate.add(new Entry(hr, index));

                            labels.add(workout.getFitTimeValues()[i] + "");
                        }
                    }

                    LineDataSet datasetCadence = new LineDataSet(entriesCadence, "# of Calls");
                    datasetCadence.setDrawFilled(true);
                    datasetCadence.setDrawCubic(true);
                    datasetCadence.setDrawCircles(false);
                    datasetCadence.setColor(getResources().getColor(R.color.colorAccent));
                    datasetCadence.setFillColor(getResources().getColor(R.color.colorAccent));
                    LineData data = new LineData(labels, datasetCadence);
                    lineChartCadence.setData(data);
                    lineChartCadence.setDescription("");
                    lineChartCadence.getLegend().setEnabled(false);

                    LineDataSet datasetElevation = new LineDataSet(entriesElevation, "nsrtnrst");
                    datasetElevation.setDrawFilled(true);
                    datasetElevation.setDrawCubic(true);
                    datasetElevation.setDrawCircles(false);
                    datasetElevation.setColor(getResources().getColor(R.color.colorAccent));
                    datasetElevation.setFillColor(getResources().getColor(R.color.colorAccent));
                    LineData dataElevation = new LineData(labels, datasetElevation);
                    lineChartElevation.setData(dataElevation);
                    lineChartElevation.setDescription("");
                    lineChartElevation.getLegend().setEnabled(false);

                    LineDataSet datasetSpeed = new LineDataSet(entriesSpeed, "# of Calls");
                    datasetSpeed.setDrawFilled(true);
                    datasetSpeed.setDrawCubic(true);
                    datasetSpeed.setDrawCircles(false);
                    datasetSpeed.setColor(getResources().getColor(R.color.colorAccent));
                    datasetSpeed.setFillColor(getResources().getColor(R.color.colorAccent));
                    LineData dataSpeed = new LineData(labels, datasetSpeed);
                    lineChartSpeed.setData(dataSpeed);
                    lineChartSpeed.setDescription("");
                    lineChartSpeed.getLegend().setEnabled(false);

                    LineDataSet datasetHeartRate = new LineDataSet(entriesHeartRate, "# of Calls");
                    datasetHeartRate.setDrawFilled(true);
                    datasetHeartRate.setDrawCubic(true);
                    datasetHeartRate.setDrawCircles(false);
                    datasetHeartRate.setColor(getResources().getColor(R.color.colorAccent));
                    datasetHeartRate.setFillColor(getResources().getColor(R.color.colorAccent));
                    LineData dataHeartRate = new LineData(labels, datasetHeartRate);
                    lineChartHeartRate.setData(dataHeartRate);
                    lineChartHeartRate.setDescription("");
                    lineChartHeartRate.getLegend().setEnabled(false);

                    LineDataSet datasetPower = new LineDataSet(entriesPower, "# of Calls");
                    datasetPower.setDrawFilled(true);
                    datasetPower.setDrawCubic(true);
                    datasetPower.setDrawCircles(false);
                    datasetPower.setColor(getResources().getColor(R.color.colorAccent));
                    datasetPower.setFillColor(getResources().getColor(R.color.colorAccent));
                    LineData dataPower = new LineData(labels, datasetPower);
                    lineChartPower.setData(dataPower);
                    lineChartPower.setDescription("");
                    lineChartPower.getLegend().setEnabled(false);
                }

                @Override
                public void onError(String error, String reason, String details) {

                }
            });

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return OpenedWorkoutDetailsFragment.newInstance(workout);
            } else if (position == 1) {
                return OpenedWorkoutCRsFragment.newInstance(workout);
            } else if (position == 2 && workout.isFitLinked()) {
                return OpenedWorkoutGraphsFragment.newInstance(workout);
            } else {
                return OpenedWorkoutDetailsFragment.newInstance(workout);
            }
        }

        @Override
        public int getCount() {
            return workout.isFitLinked() ? 3 : 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "DETAILS";
            } else if (position == 1) {
                return "RETOURS";
            } else if (position == 2 && workout.isFitLinked()) {
                return "ANALYSE .FIT";
            } else {
                return null;
            }
        }
    }

    public void setText(Integer id, Object txt) {
        try {
            ((TextView) getWindow().findViewById(id)).setText(txt.toString());
        } catch (Exception e ) {
            System.out.println(e.toString());
        }
    }

    public void toast(String str) {
        Context context = getApplicationContext();
        CharSequence text = str;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


}
