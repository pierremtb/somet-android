package io.somet.somet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import im.delight.android.ddp.MeteorSingleton;
import io.somet.somet.R;
import io.somet.somet.Somet;
import io.somet.somet.adapters.MyAthletesAdapter;
import io.somet.somet.adapters.WorkoutsAdapter;
import io.somet.somet.data.Plan;
import io.somet.somet.data.User;
import io.somet.somet.data.Workout;
import io.somet.somet.helpers.ItemClickSupport;
import io.somet.somet.helpers.Tools;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Somet app;

    static String profile_username;
    boolean isMe, isMyTrainer, isMyAthlete;
    User user;


    RecyclerView rvItems;
    List<User> allMyAthletes;
    MyAthletesAdapter adapter;

    CardView profileInformationsCard, profileInformationsTrainerCard, profileInformationsAthletesCard, profileInformationsStravaCard;
    TextView profileUsername, profileEmail, profileWeight, profileHeight, profileTrainerName, profileStravaLink;
    LinearLayout profileUsernameRow, profileEmailRow, profileWeightRow, profilHeightRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (Somet) getApplicationContext();

        Bundle b = getIntent().getExtras();
        profile_username = b.getString("username");
        isMe = profile_username.equals(app.getCurrentUser().getUsername());
        user = new User(MeteorSingleton.getInstance().getDatabase().getCollection("users").whereEqual("username", profile_username).findOne());
        isMyTrainer = profile_username.equals(app.getCurrentUser().getMyTrainer());

        String[] my_athletes = user.getMyAthletes();

        isMyAthlete = Arrays.asList(my_athletes).contains(profile_username);


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
        profileTrainerName.setOnClickListener(this);

        prepareProfile();

        if(user.isTrainer()) {

            rvItems = (RecyclerView) findViewById(R.id.rvMyAthletes);

            allMyAthletes = new ArrayList<>();

            for (String usr : my_athletes) {
                System.out.println(usr);
                allMyAthletes.add(new User(MeteorSingleton.getInstance().getDatabase().getCollection("users").whereEqual("username", usr).findOne()));
            }

            adapter = new MyAthletesAdapter(allMyAthletes);
            rvItems.setAdapter(adapter);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            rvItems.setLayoutManager(linearLayoutManager);

            ItemClickSupport.addTo(rvItems).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    Bundle b = new Bundle();
                    b.putString("username", allMyAthletes.get(position).getUsername());
                    intent.putExtras(b);
                    startActivityForResult(intent, 0);
                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if(isMe) {
            fab.setImageResource(R.drawable.ic_mode_edit);
        } else if(isMyTrainer || isMyAthlete) {
            fab.setImageResource(R.drawable.ic_delete);
        } else {
            fab.setImageResource(R.drawable.ic_add);
        }

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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareProfile() {
        if(user.isTrainer()) {
            profileInformationsAthletesCard.setVisibility(View.VISIBLE);
        }
        else {
            profileInformationsTrainerCard.setVisibility(View.VISIBLE);
            try {
                profileTrainerName.setText(user.getMyTrainer());
            } catch (Exception e) {

            }
        }

        if(user.isStravaSynced()) {
            profileInformationsStravaCard.setVisibility(View.VISIBLE);
        }

        setTitle(user.getCompleteName());
        try {
            profileUsername.setText(String.format("@%s", user.getUsername()));
        } catch (Exception e) {  }
        try {
            profileEmail.setText(String.format("%s", user.getEmail()));
        } catch (Exception e) {}
        try {
            profileWeight.setText(String.format("%.1fkg", user.getWeight()));
        } catch (Exception e) {}
        try {
            profileHeight.setText(String.format("%.1fcm", user.getHeight()));
        } catch (Exception e) {}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileTrainerName:
                openProfile(user.getMyTrainer());
                break;
        }
    }


    public void openProfile(String username) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        Bundle b = new Bundle();
        b.putString("username", username);
        intent.putExtras(b);
        startActivityForResult(intent, 0);
    }

}
