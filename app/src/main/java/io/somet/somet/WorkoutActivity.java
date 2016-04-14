package io.somet.somet;

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

public class WorkoutActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    View view;

    public MaterialDialog loadingDialog;

    public HashMap<String, Object> workout = new HashMap<>();
    public static String wk_id = "";
    public static boolean is_fit = false;

    public TextView openedWkTitle;
    public TextView openedWkDescription;
    public TextView openedWkDuration;
    public TextView openedWkDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chargement");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
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

        loadingDialog = new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();

        Bundle b = getIntent().getExtras();
        wk_id = b.getString("id");

        MeteorSingleton.getInstance().subscribe("workoutOfThisId", new Object[]{ wk_id }, new SubscribeListener() {
            @Override
            public void onSuccess() {
                settleWorkout();
                loadingDialog.dismiss();
            }

            @Override
            public void onError(String error, String reason, String details) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workout, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    
    public static class OpenedWorkoutDetailsFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public OpenedWorkoutDetailsFragment() {
        }

        public static OpenedWorkoutDetailsFragment newInstance() {
            OpenedWorkoutDetailsFragment fragment = new OpenedWorkoutDetailsFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_workout_details, container, false);
            return rootView;
        }
    }

    public static class OpenedWorkoutCRsFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public OpenedWorkoutCRsFragment() {
        }

        public static OpenedWorkoutCRsFragment newInstance() {
            OpenedWorkoutCRsFragment fragment = new OpenedWorkoutCRsFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            System.out.println("YYYYYYYYYYYYy");

            View rootView = inflater.inflate(R.layout.fragment_workout_crs, container, false);

            HashMap<String, Object> cr = (HashMap<String, Object>) MeteorSingleton.getInstance().getDatabase().getCollection("workouts").getDocument(wk_id).getField("cr");
            float cr_eff = 0, cr_ple = 0, cr_sensa = 0, cr_mood = 0;
            try {
                cr_eff = Float.parseFloat(cr.get("effort").toString());
            } catch (Exception e ) {}
            try {
                cr_ple = Float.parseFloat(cr.get("pleasure").toString());
            } catch (Exception e ) {}
            try {
                cr_sensa = Float.parseFloat(cr.get("sensations").toString());
            } catch (Exception e ) {}
            try {
                cr_mood = Float.parseFloat(cr.get("mood").toString());
            } catch (Exception e ) {}


            drawCircle(R.id.arcCREffort, rootView, cr_eff);
            drawCircle(R.id.arcCRPleasure, rootView, cr_ple);
            drawCircle(R.id.arcCRSensations, rootView, cr_sensa);
            drawCircle(R.id.arcCRMood, rootView, cr_mood);

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
        private static final String ARG_SECTION_NUMBER = "section_number";

        public OpenedWorkoutGraphsFragment() {
        }

        public static OpenedWorkoutGraphsFragment newInstance() {
            OpenedWorkoutGraphsFragment fragment = new OpenedWorkoutGraphsFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_workout_graphs, container, false);

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return OpenedWorkoutDetailsFragment.newInstance();
                case 1:
                    return OpenedWorkoutCRsFragment.newInstance();
                case 2:
                    return OpenedWorkoutGraphsFragment.newInstance();
                default:
                    return OpenedWorkoutDetailsFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "DETAILS";
                case 1:
                    return "SENSATIONS";
                case 2:
                    return "GRAPHIQUES";
            }
            return null;
        }
    }

    public void settleWorkout() {
        workout = Tools.getMap(MeteorSingleton.getInstance().getDatabase().getCollection("workouts").getDocument(wk_id));
        System.out.println(workout);

        setText(R.id.openedWkDuration , Tools.dispDuration(workout.get("duration")));
        setText(R.id.openedWkDescription, workout.get("description"));
        setText(R.id.openedWkComments, workout.get("comments"));
        setText(R.id.openedWkDate, Tools.dispDate(workout.get("start_date")));

        getSupportActionBar().setTitle(workout.get("title").toString());
        HashMap<String, Object> fit_values = (HashMap<String, Object>) workout.get("fit_values");

        is_fit = !(fit_values.get("time") instanceof String);

        if(is_fit) {

            HashMap<String, Object> time = (HashMap<String, Object>) fit_values.get("time");
            HashMap<String, Object> cadence = (HashMap<String, Object>) fit_values.get("cadence");
            HashMap<String, Object> elevation = (HashMap<String, Object>) fit_values.get("elevation");
            HashMap<String, Object> speed = (HashMap<String, Object>) fit_values.get("speed");
            HashMap<String, Object> heart_rate = (HashMap<String, Object>) fit_values.get("heart_rate");
            HashMap<String, Object> power = (HashMap<String, Object>) fit_values.get("power");

            ArrayList<Object> time_values = new ArrayList<>();
            ArrayList<Object> cadence_values = new ArrayList<>();
            ArrayList<Object> elevation_values = new ArrayList<>();
            ArrayList<Object> speed_values = new ArrayList<>();
            ArrayList<Object> heart_rate_values = new ArrayList<>();
            ArrayList<Object> power_values = new ArrayList<>();

            time_values = (ArrayList<Object>) time.get("values");
            cadence_values = (ArrayList<Object>) cadence.get("values");
            elevation_values = (ArrayList<Object>) elevation.get("values");
            speed_values = (ArrayList<Object>) speed.get("values");
            heart_rate_values = (ArrayList<Object>) heart_rate.get("values");
            power_values = (ArrayList<Object>) power.get("values");


            System.out.println(speed_values);
            System.out.println(heart_rate_values);
            System.out.println(power_values);

            LineChart lineChartCadence = (LineChart) findViewById(R.id.chartCadence);
            LineChart lineChartElevation = (LineChart) findViewById(R.id.chartElevation);
            LineChart lineChartSpeed = (LineChart) findViewById(R.id.chartSpeed);
            LineChart lineChartHeartRate = (LineChart) findViewById(R.id.chartHeartRate);
            LineChart lineChartPower = (LineChart) findViewById(R.id.chartPower);

            ArrayList<Entry> entriesCadence = new ArrayList<>();
            ArrayList<Entry> entriesElevation = new ArrayList<>();
            ArrayList<Entry> entriesSpeed = new ArrayList<>();
            ArrayList<Entry> entriesHeartRate = new ArrayList<>();
            ArrayList<Entry> entriesPower = new ArrayList<>();

            ArrayList<String> labels = new ArrayList<>();

            for (int i = 0; i < time_values.size(); i++) {
                if (i % 10 == 0) {
                    Integer index = time_values.get(i) != null && !time_values.isEmpty() ? Double.valueOf(time_values.get(i).toString()).intValue() : 0;
                    Integer cad = cadence_values.get(i) != null && !cadence_values.isEmpty() ? Double.valueOf(cadence_values.get(i).toString()).intValue() : 0;
                    Integer hr = heart_rate_values.get(i) != null && !heart_rate_values.isEmpty() ? Double.valueOf(heart_rate_values.get(i).toString()).intValue() : 0;
                    Integer elev = elevation_values != null && !elevation_values.isEmpty() ? Double.valueOf(elevation_values.get(i).toString()).intValue() : 0;
                    Integer sp = speed_values.get(i) != null && !speed_values.isEmpty() ? Double.valueOf(speed_values.get(i).toString()).intValue() : 0;
                    Integer pw = power_values.get(i) != null && !power_values.isEmpty() ? Double.valueOf(power_values.get(i).toString()).intValue() : 0;

                    entriesCadence.add(new Entry(cad, index));
                    entriesElevation.add(new Entry(elev, index));
                    entriesSpeed.add(new Entry(sp, index));
                    entriesPower.add(new Entry(pw, index));
                    entriesHeartRate.add(new Entry(hr, index));

                    labels.add(time_values.get(i).toString());
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
