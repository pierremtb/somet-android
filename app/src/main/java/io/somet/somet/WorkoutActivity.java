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
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

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
        String id = b.getString("id");

        MeteorSingleton.getInstance().subscribe("workoutOfThisId", new Object[]{ id }, new SubscribeListener() {
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

            HashMap<String, Object> cr = (HashMap<String, Object>) MeteorSingleton.getInstance().getDatabase().getCollection("workouts").findOne().getField("cr");
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

            SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                    .setRange(0, 10, 0)
                    .setLineWidth(32f)
                    .build();

            int series1Index = arcView1.addSeries(seriesItem1);

            arcView1.addEvent(new DecoEvent.Builder(DecoEvent.EventType.EVENT_SHOW, true)
                    .setDelay(0)
                    .setDuration(500)
                    .build());

            arcView1.addEvent(new DecoEvent.Builder(value).setIndex(series1Index).setDelay(1000).build());
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
        workout = MainActivity.getMap(MeteorSingleton.getInstance().getDatabase().getCollection("workouts").findOne());
        System.out.println(workout);

        setText(R.id.openedWkDuration , MainActivity.dispDuration(workout.get("duration")));
        setText(R.id.openedWkDescription, workout.get("description"));
        setText(R.id.openedWkComments, workout.get("comments"));
        setText(R.id.openedWkDate, MainActivity.dispDate(workout.get("start_date")));

        getSupportActionBar().setTitle(workout.get("title").toString());

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
