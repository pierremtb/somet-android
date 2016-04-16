package io.somet.somet;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.db.Document;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    OnFragmentInteractionListener Main;

    Workout last_wk;
    Plan today_pl;

    TextView wkDetailsButton, lastWkTitle, lastWkDescription, lastWkDuration, lastWkDate, otherWksButton, todayPlDescription, todayPlDuration, todayPlType, todayPlSupport;

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        wkDetailsButton = (TextView) myView.findViewById(R.id.lastWkDetails);
        wkDetailsButton.setOnClickListener(this);

        otherWksButton = (TextView) myView.findViewById(R.id.otherWks);
        otherWksButton.setOnClickListener(this);

        lastWkTitle = (TextView) myView.findViewById(R.id.lastWkTitle);
        lastWkDescription = (TextView) myView.findViewById(R.id.lastWkDescription);
        lastWkDuration = (TextView) myView.findViewById(R.id.lastWkDuration);
        lastWkDate = (TextView) myView.findViewById(R.id.lastWkDate);

        todayPlDescription = (TextView) myView.findViewById(R.id.todayPlDescription);
        todayPlDuration = (TextView) myView.findViewById(R.id.todayPlDuration);
        todayPlType = (TextView) myView.findViewById(R.id.todayPlType);
        todayPlSupport = (TextView) myView.findViewById(R.id.todayPlSupport);

        String usr = (String) (Main.isTrainer() ? Main.getSelectedAthlete() : Main.getUser().get("username"));
        System.out.println(usr);

        setLastWkCard();
        setTodayPlCard();

        return myView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            Main = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.otherWks:
                Main.openWorkouts();
                break;
            case R.id.lastWkDetails:
                System.out.println(last_wk.getTitle());
                Main.openWorkout(last_wk.getId());
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void toast(String str);

        HashMap<String, Object> getUser();

        void setText(Integer id, Object txt);

        void openWorkout(Object id);

        void openWorkouts();

        boolean isTrainer();

        String getSelectedAthlete();
    }

    public void setLastWkCard() {
        Document[] workouts = MeteorSingleton.getInstance().getDatabase().getCollection("workouts").whereEqual("owner", (String) (Main.isTrainer() ? Main.getSelectedAthlete() : Main.getUser().get("username"))).find();
        Arrays.sort(workouts, new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                return Tools.getDate(o1.getField("start_date"))
                        .compareTo(Tools.getDate(o2.getField("start_date")));
            }
        });
        last_wk = new Workout(workouts[workouts.length - 1]);
        System.out.println(last_wk.toString());
        System.out.println(last_wk);
        try {
            lastWkTitle.setText(last_wk.getTitle());
        } catch (Exception e) {
            lastWkTitle.setVisibility(View.GONE);
        }
        try {
            lastWkDescription.setText(last_wk.getDescription());
        } catch (Exception e) {
            lastWkDescription.setVisibility(View.GONE);
        }
        try {
            lastWkDuration.setText(Tools.dispDuration(last_wk.getDuration()));
        } catch (Exception e) {
            lastWkDuration.setVisibility(View.GONE);
        }
        try {
            lastWkDate.setText(Tools.dispDate(last_wk.getStartDate()));
        } catch (Exception e) {
            lastWkDate.setVisibility(View.GONE);
        }
    }

    public void setTodayPlCard() {
        Document[] plans = MeteorSingleton.getInstance().getDatabase().getCollection("plans").whereEqual("owner", (String) (Main.isTrainer() ? Main.getSelectedAthlete() : Main.getUser().get("username"))).find();
        Arrays.sort(plans, new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                return Tools.getDate(o1.getField("monday_date"))
                        .compareTo(Tools.getDate(o2.getField("monday_date")));
            }
        });
        today_pl = new Plan(plans[plans.length - 1]);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if(c.getTime() == today_pl.getMondayDate()) {
            Calendar t = Calendar.getInstance();
            int index = t.get(Calendar.DAY_OF_WEEK);

            todayPlDescription.setText(today_pl.getDays().get(index).get("description"));
            todayPlDuration.setText(today_pl.getDays().get(index).get("duration"));
            todayPlType.setText(today_pl.getDays().get(index).get("type"));
            todayPlSupport.setText(today_pl.getDays().get(index).get("support"));
        } else {
            todayPlDescription.setText("Pas de plan cette semaine");
            todayPlSupport.setVisibility(View.GONE);
            todayPlDuration.setVisibility(View.GONE);
            todayPlType.setVisibility(View.GONE);
        }
    }
}
