package io.somet.somet.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.SubscribeListener;
import im.delight.android.ddp.db.Document;
import im.delight.android.ddp.db.memory.InMemoryDatabase;
import io.somet.somet.R;
import io.somet.somet.fragments.AnalysisFragment;
import io.somet.somet.fragments.CalendarFragment;
import io.somet.somet.fragments.DashboardFragment;

public class MainActivity
        extends AppCompatActivity
        implements MeteorCallback,
            DashboardFragment.OnFragmentInteractionListener,
            CalendarFragment.OnFragmentInteractionListener,
            AnalysisFragment.OnFragmentInteractionListener,
            ActionBar.OnNavigationListener{

    private static final int REQUEST_LOGIN = 0;
    public Meteor meteor;
    private HashMap<String, Object> User = new HashMap<>();
    public boolean isTrainer = false;
    public String selectedAthlete = "";
    public ArrayList<String> athletesOfTrainer = new ArrayList<>();
    public boolean subscribed = false;

    public static MaterialDialog loadingDialog;
    private BottomBar mBottomBar;
    public ActionBar actionBar;

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    public boolean isTrainer() {
        return isTrainer;
    }

    public String getSelectedAthlete() {
        return selectedAthlete;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        isTrainer = false;

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.bottom_bar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if(subscribed) {
                    switch (menuItemId) {
                        case R.id.bottomBarDashboard:
                            showDashboard();
                            break;
                        case R.id.bottomBarCalendar:
                            showCalendar();
                            break;
                        case R.id.bottomBarAnalysis:
                            showAnalysis();
                            break;/*
                        case R.id.bottomBarProfile:
                            showProfile();
                            break;*/
                    }
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bottomBarDashboard) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });

        mBottomBar.mapColorForTab(0, "#7B1FA2");
        mBottomBar.mapColorForTab(1, "#FF5252");
        mBottomBar.mapColorForTab(2, "#FF9800");
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
                .theme(Theme.LIGHT)
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
                isTrainer = false;
                meteor.logout();
                launchLogin();
                return true;
            case R.id.action_profile:
                openProfile();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public void onConnect(final boolean signedInAutomatically) {
        if (signedInAutomatically) {
            prepareBoard(false);
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
        if(subscribed) {
            startLoadingDialog(this);
            selectedAthlete = athletesOfTrainer.get(position);
            showDashboard();
            return true;
        }
        else
            return false;
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
                prepareBoard(true);
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
        dismissLoadingDialog();
        System.out.println("--LOG-COLLECTIONS--");
        System.out.println(Arrays.toString(meteor.getDatabase().getCollectionNames()));
        System.out.println(meteor.getDatabase().getCollection("workouts").count());
        System.out.println("--ENDLOG--");
    }

    public void showCalendar() {
        Fragment CalendarFG = new CalendarFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, CalendarFG);
        ft.commit();
    }

    public void showAnalysis() {
        Fragment AnalysisFG = new AnalysisFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, AnalysisFG);
        ft.commit();
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

    public void openWorkouts() {
        Intent intent = new Intent(getApplicationContext(), WorkoutsActivity.class);
        Bundle b = new Bundle();
        b.putString("owner", isTrainer ? selectedAthlete : User.get("username").toString());
        intent.putExtras(b);
        startActivityForResult(intent, 0);
    }

    public void openProfile() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivityForResult(intent, 0);
    }

    public void toast(String str) {
        Context context = getApplicationContext();
        CharSequence text = str;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void prepareBoard(final boolean doGet) {
        if(true) {
            meteor.subscribe("mainUserDataSync", new Object[]{}, new SubscribeListener() {
                @Override
                public void onSuccess() {
                    setUserData();
                    prepareActionBar();
                    showDashboard();
                }

                @Override
                public void onError(String error, String reason, String details) {
                    toast(error + "-" + reason);
                }
            });
        } else {
            setUserData();
            prepareActionBar();
            showDashboard();
        }
    }

    public void setUserData() {
        Document user_doc = meteor.getDatabase().getCollection("users").findOne();
        User.put("profile", user_doc.getField("profile"));
        User.put("username", user_doc.getField("username"));
        subscribed = true;
        HashMap<String, Object> pro = (HashMap<String, Object>) user_doc.getField("profile");
        if(pro.containsKey("trainer"))
            if(((Boolean) pro.get("trainer")))
                isTrainer = true;
    }

    public ArrayAdapter<String> adapter;

    public void finishActionBar() {
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        adapter = new ArrayAdapter<String>(actionBar.getThemedContext(),
                android.R.layout.simple_spinner_item, android.R.id.text1,
                athletesOfTrainer);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(adapter, this);
    }

    public void prepareActionBar() {
        if(isTrainer) {
            setAthletesOfTrainer();
            finishActionBar();
        } else {
            actionBar.setDisplayShowTitleEnabled(true);
            if(adapter != null) {
                adapter.clear();
                actionBar.setListNavigationCallbacks(adapter,this);
            }
        }
    }

    public void setAthletesOfTrainer() {
        Document[] athletes = meteor.getDatabase().getCollection("athletes").find();
        athletesOfTrainer = new ArrayList<>();
        for (Document athlete : athletes) {
            athletesOfTrainer.add(athlete.getField("username").toString());
        }
        selectedAthlete = athletesOfTrainer.size() > 0 ? athletesOfTrainer.get(0) : "";
    }

    public void setText(Integer id, Object txt) {
        try {
            ((TextView) findViewById(id)).setText(txt.toString());
        } catch (Exception e ) {}
    }

    public HashMap<String, Object> getUser() {
        return User;
    }

    public static void startLoadingDialog(Context context) {
        loadingDialog = new MaterialDialog.Builder(context)
            .content(R.string.please_wait)
            .theme(Theme.LIGHT)
            .progress(true, 0)
            .show();
    }

    public static void dismissLoadingDialog() {
        loadingDialog.dismiss();
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
