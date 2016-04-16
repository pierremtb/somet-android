package io.somet.somet.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import im.delight.android.ddp.MeteorSingleton;
import io.somet.somet.R;
import io.somet.somet.helpers.Tools;

public class ProfileActivity extends AppCompatActivity {

    HashMap<String, Object> user = new HashMap<>();
    HashMap<String, Object> profile = new HashMap<>();
    boolean isTrainer = false, isStrava = false;

    CardView profileInformationsCard, profileInformationsTrainerCard, profileInformationsAthletesCard, profileInformationsStravaCard;
    TextView profileUsername, profileEmail, profileWeight, profileHeight, profileTrainerName, profileStravaLink;
    LinearLayout profileUsernameRow, profileEmailRow, profileWeightRow, profilHeightRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileInformationsStravaCard = (CardView) findViewById(R.id.profileInformationsStravaCard);
        profileInformationsAthletesCard = (CardView) findViewById(R.id.profileInformationsAthletesCard);
        profileInformationsTrainerCard = (CardView) findViewById(R.id.profileInformationsTrainerCard);

        profileUsername = (TextView) findViewById(R.id.profileUsername);
        profileEmail = (TextView) findViewById(R.id.profileEmail);
        profileWeight = (TextView) findViewById(R.id.profileWeight);
        profileHeight = (TextView) findViewById(R.id.profileHeight);

        prepareUserInformations();
        prepareProfile();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    private void prepareUserInformations() {
        user = Tools.getMap(MeteorSingleton.getInstance().getDatabase().getCollection("users").findOne());
        profile = (HashMap<String, Object>) user.get("profile");
        if(profile.containsKey("trainer"))
            if(((Boolean) profile.get("trainer")))
                isTrainer = true;
        if(profile.containsKey("finished_set_up") && profile.containsKey("strava_sync"))
            if(((Boolean) profile.get("finished_set_up")))
                isStrava = true;
    }

    private void prepareProfile() {
        if(isTrainer) {
            profileInformationsAthletesCard.setVisibility(View.VISIBLE);
        }
        else {
            profileInformationsTrainerCard.setVisibility(View.VISIBLE);
        }

        if(isStrava) {
            profileInformationsStravaCard.setVisibility(View.VISIBLE);
        }

        setTitle(profile.get("complete_name").toString());
        try {
            profileUsername.setText(String.format("@%s", user.get("username").toString()));
        } catch (Exception e) {

        }
        try {
            profileEmail.setText(String.format("%s", profile.get("email").toString()));
        } catch (Exception e) {}
        try {
            profileWeight.setText(String.format("%.1fkg", Float.valueOf(profile.get("weight").toString())));
        } catch (Exception e) {}
        try {
            profileHeight.setText(String.format("%.1fcm", Float.valueOf(profile.get("height").toString())));
        } catch (Exception e) {}
    }
}
