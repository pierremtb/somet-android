package io.somet.somet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.SubscribeListener;
import im.delight.android.ddp.db.Document;
import im.delight.android.ddp.db.memory.InMemoryDatabase;

public class MainActivity
        extends AppCompatActivity
        implements MeteorCallback,
            DashboardFragment.OnFragmentInteractionListener,
            CalendarFragment.OnFragmentInteractionListener,
            AnalysisFragment.OnFragmentInteractionListener,
            ProfileFragment.OnFragmentInteractionListener,
            ActionBar.OnNavigationListener{

    private static final int REQUEST_LOGIN = 0;
    public Meteor meteor;
    private HashMap<String, Object> User = new HashMap<>();

    public MaterialDialog loadingDialog;
    private BottomBar mBottomBar;

    public ActionBar actionBar;
    public boolean isTrainer = false;

    public boolean isTrainer() {
        return isTrainer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.bottom_bar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.bottomBarDashboard:
                        showDashboard();
                        break;
                    case R.id.bottomBarCalendar:
                        showCalendar();
                        break;
                    case R.id.bottomBarAnalysis:
                        showAnalysis();
                        break;
                    case R.id.bottomBarProfile:
                        showProfile();
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarDashboard) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        meteor = MeteorSingleton.createInstance(this, "ws://somet.herokuapp.com/websocket", new InMemoryDatabase());
        meteor.addCallback(this);
        meteor.connect();

        loadingDialog = new MaterialDialog.Builder(this)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about :
                return true;
            case R.id.action_logout:
                meteor.logout();
                isTrainer = false;
                launchLogin();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public void onConnect(final boolean signedInAutomatically) {
        if (signedInAutomatically) {
            prepareBoard();
        } else {
            launchLogin();
        }
    }

    @Override
    public void onDisconnect() {
    }

    @Override
    public void onDataAdded(String collectionName, String documentID, String fieldsJson) {
    }

    @Override
    public void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson) {

    }

    @Override
    public void onDataRemoved(String collectionName, String documentID) {
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
        return true;
    }

    @Override
    public void onException(Exception e) {
        System.out.println("Exception");
        if (e != null) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == REQUEST_LOGIN) {
                prepareBoard();
            }
        }
    }

    @Override
    public void onDestroy() {
        meteor.disconnect();
        meteor.removeCallback(this);

        super.onDestroy();
    }

    public void showDashboard() {
        Fragment DashboardFG = new DashboardFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, DashboardFG);
        ft.commit();
        getSupportActionBar().setTitle(R.string.nav_dashboard);
    }

    public void showCalendar() {
        Fragment CalendarFG = new CalendarFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, CalendarFG);
        ft.commit();
        getSupportActionBar().setTitle(R.string.nav_calendar);
    }

    public void showAnalysis() {
        Fragment AnalysisFG = new AnalysisFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, AnalysisFG);
        ft.commit();
        getSupportActionBar().setTitle(R.string.nav_analysis);
    }

    public void showProfile() {
        Fragment ProfileFG = new ProfileFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, ProfileFG);
        ft.commit();
        getSupportActionBar().setTitle(R.string.nav_profile);
    }

    public void launchLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    public void openWorkout(Object id) {
        Intent intent = new Intent(getApplicationContext(), WorkoutActivity.class);
        Bundle b = new Bundle();
        b.putString("id", id.toString());
        intent.putExtras(b);
        startActivityForResult(intent, 0);
    }

    public void toast(String str) {
        Context context = getApplicationContext();
        CharSequence text = str;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void prepareBoard() {
        meteor.subscribe("getUserData", new Object[]{}, new SubscribeListener() {
            @Override
            public void onSuccess() {
                Document user_doc = meteor.getDatabase().getCollection("users").findOne();
                User.put("profile", user_doc.getField("profile"));
                User.put("username", user_doc.getField("username"));
                loadingDialog.dismiss();
                HashMap<String, String> pro = (HashMap<String, String>) user_doc.getField("profile");
                System.out.println(pro);
                if(pro.containsKey("trainer")) {
                    isTrainer = true;
                }
                toast(isTrainer() + "");
                prepareActionBar();
                showDashboard();
            }

            @Override
            public void onError(String error, String reason, String details) {

            }
        });
    }

    public ArrayAdapter<String> adapter;

    public void finishActionBar(ArrayList<String> dropdownValues) {
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        adapter = new ArrayAdapter<String>(actionBar.getThemedContext(),
                android.R.layout.simple_spinner_item, android.R.id.text1,
                dropdownValues);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(adapter, this);
    }

    public void prepareActionBar() {
        if(isTrainer) {
            meteor.subscribe("athletesOfCurrentUserSync", new Object[]{}, new SubscribeListener() {
                @Override
                public void onSuccess() {
                    Document[] athletes = meteor.getDatabase().getCollection("athletes").find();
                    ArrayList<String> dropdownAthletes = new ArrayList<>();
                    for (Document athlete : athletes) {
                        dropdownAthletes.add(athlete.getField("username").toString());
                    }
                    System.out.println(dropdownAthletes);
                    finishActionBar(dropdownAthletes);
                }

                @Override
                public void onError(String error, String reason, String details) {

                }
            });
        }
        else {
            actionBar.setDisplayShowTitleEnabled(true);
            if(adapter != null) {
                adapter.clear();
                actionBar.setListNavigationCallbacks(adapter,this);
            }
        }
    }

    public void setText(Integer id, Object txt) {
        try {
            ((TextView) findViewById(id)).setText(txt.toString());
        } catch (Exception e ) {}
    }

    public static HashMap<String, Object> getMap(Document doc) {
        HashMap<String, Object> obj = new HashMap<>();
        if(doc != null) {
            for (String f : doc.getFieldNames()) {
                obj.put(f, doc.getField(f));
            }
            obj.put("_id", doc.getId());
        }
        return obj;
    }

    public static String dispDuration(Object str) {
        int sec = 0;
        try{
            sec = Integer.parseInt(str.toString());
        } catch (NumberFormatException e) {
            //Will Throw exception!
            //do something! anything to handle the exception.
        }
        return String.format("%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, sec % 60);
    }

    public static String dispDate(Object str) {
        HashMap<String, Long> t = (HashMap<String, Long>) str;
        long timestamp = 0;
        try {
            timestamp = t.get("$date");
        } catch (Exception e ) {}
        Date date = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public HashMap<String, Object> getUser() {
        return User;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
