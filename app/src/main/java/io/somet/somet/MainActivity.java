package io.somet.somet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.HashMap;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.SubscribeListener;
import im.delight.android.ddp.db.Document;
import im.delight.android.ddp.db.memory.InMemoryDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MeteorCallback, DashboardFragment.OnFragmentInteractionListener {

    private Meteor mMeteor;
    private static final int REQUEST_LOGIN = 0;
    private HashMap<String, Object> User = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mMeteor = new Meteor(this, "ws://somet.herokuapp.com/websocket", new InMemoryDatabase());
        mMeteor.addCallback(this);
        mMeteor.connect();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            showDashboard();
        } else if (id == R.id.nav_workouts) {
            toast("Workouts");

        } else if (id == R.id.nav_plans) {
            toast("P");

        } else if (id == R.id.nav_events) {
            toast("E");

        } else if (id == R.id.nav_calendar) {
            toast("C");

        } else if (id == R.id.nav_analysis) {
            toast("A");

        } else if (id == R.id.nav_settings) {
            toast("S");

        }else if (id == R.id.nav_logout) {
            mMeteor.logout();
            launchLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        mMeteor.disconnect();
        mMeteor.removeCallback(this);

        super.onDestroy();
    }

    public void showDashboard() {
        Fragment DashboardFG = new DashboardFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, DashboardFG);
        ft.commit();
    }

    public void launchLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    @Override
    public void toast(String str) {
        Context context = getApplicationContext();
        CharSequence text = str;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void setDrawerData() {
        TextView username = (TextView) findViewById(R.id.username);
        TextView cn = (TextView) findViewById(R.id.completeName);
        username.setText((String) User.get("username"));
        cn.setText((String) ((HashMap<String, Object>) User.get("profile")).get("complete_name"));
    }

    public void prepareBoard() {
        mMeteor.subscribe("getUserData", new Object[]{}, new SubscribeListener() {
            @Override
            public void onSuccess() {
                Document user_doc = mMeteor.getDatabase().getCollection("users").getDocument(mMeteor.getUserId());
                User.put("profile", user_doc.getField("profile"));
                User.put("username", user_doc.getField("username"));
                System.out.println(User);
                setDrawerData();
                toast("Bienvenue @" + user_doc.getField("username") + ".");
                showDashboard();
            }

            @Override
            public void onError(String error, String reason, String details) {

            }
        });
    }

    public HashMap<String, Object> getUser() {
        return User;
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
