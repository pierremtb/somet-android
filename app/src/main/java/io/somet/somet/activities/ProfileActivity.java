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
import io.somet.somet.Somet;
import io.somet.somet.helpers.Tools;

public class ProfileActivity extends AppCompatActivity {
    Somet app;

    CardView profileInformationsCard, profileInformationsTrainerCard, profileInformationsAthletesCard, profileInformationsStravaCard;
    TextView profileUsername, profileEmail, profileWeight, profileHeight, profileTrainerName, profileStravaLink;
    LinearLayout profileUsernameRow, profileEmailRow, profileWeightRow, profilHeightRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (Somet) getApplicationContext();

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

        profileTrainerName = (TextView) findViewById(R.id.profileTrainerName);

        prepareProfile();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "La modification du profil arrivera bientôt !", Snackbar.LENGTH_LONG)
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

    private void prepareProfile() {
        if(app.getCurrentUser().isTrainer()) {
            profileInformationsAthletesCard.setVisibility(View.VISIBLE);
        }
        else {
            profileInformationsTrainerCard.setVisibility(View.VISIBLE);
            try {
                profileTrainerName.setText(app.getCurrentUser().getMyTrainer());
            } catch (Exception e) {

            }
        }

        if(app.getCurrentUser().isStravaSynced()) {
            profileInformationsStravaCard.setVisibility(View.VISIBLE);
        }

        setTitle(app.getCurrentUser().getCompleteName());
        try {
            profileUsername.setText(String.format("@%s", app.getCurrentUser().getUsername()));
        } catch (Exception e) {

        }
        try {
            profileEmail.setText(String.format("%s", app.getCurrentUser().getEmail()));
        } catch (Exception e) {}
        try {
            profileWeight.setText(String.format("%.1fkg", app.getCurrentUser().getWeight()));
        } catch (Exception e) {}
        try {
            profileHeight.setText(String.format("%.1fcm", app.getCurrentUser().getHeight()));
        } catch (Exception e) {}
    }
}
