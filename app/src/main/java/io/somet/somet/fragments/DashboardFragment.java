package io.somet.somet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.db.Document;
import io.somet.somet.Somet;
import io.somet.somet.data.Plan;
import io.somet.somet.R;
import io.somet.somet.helpers.Tools;
import io.somet.somet.data.Workout;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    Somet app;

    OnFragmentInteractionListener Main;

    Workout last_wk;
    Plan today_pl;

    LinearLayout lastWkCard, todayPlCard;

    TextView lastWkTitle, lastWkDescription, lastWkDuration, lastWkDate, otherWksButton, todayPlDescription, todayPlDuration, todayPlType, todayPlSupport, otherPlsButton;

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (Somet) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        lastWkCard = (LinearLayout) myView.findViewById(R.id.cardLastWk);
        lastWkCard.setOnClickListener(this);
        todayPlCard = (LinearLayout) myView.findViewById(R.id.cardThisWeekPl);
        todayPlCard.setOnClickListener(this);
        otherWksButton = (TextView) myView.findViewById(R.id.otherWks);
        otherWksButton.setOnClickListener(this);

        otherPlsButton = (TextView) myView.findViewById(R.id.otherPls);
        otherPlsButton.setOnClickListener(this);

        lastWkTitle = (TextView) myView.findViewById(R.id.lastWkTitle);
        lastWkDescription = (TextView) myView.findViewById(R.id.lastWkDescription);
        //lastWkDuration = (TextView) myView.findViewById(R.id.lastWkDuration);
        //lastWkDate = (TextView) myView.findViewById(R.id.lastWkDate);

        todayPlDescription = (TextView) myView.findViewById(R.id.todayPlDescription);
        //todayPlDuration = (TextView) myView.findViewById(R.id.todayPlDuration);
        todayPlType = (TextView) myView.findViewById(R.id.todayPlType);
        //todayPlSupport = (TextView) myView.findViewById(R.id.todayPlSupport);

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
            case R.id.cardLastWk:
                Main.openWorkout(last_wk.getId());
                break;
            case R.id.otherPls:
                Main.openPlans();
                break;
            case R.id.cardThisWeekPl:
                Main.openPlan(today_pl.getId());
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void toast(String str);

        void setText(Integer id, Object txt);

        void openWorkout(Object id);

        void openWorkouts();

        void openPlan(Object id);

        void openPlans();
    }

    public void setLastWkCard() {
        Document[] workouts = MeteorSingleton.getInstance().getDatabase().getCollection("workouts").whereEqual("owner", app.getTargetedUser().getUsername()).find();
        Arrays.sort(workouts, new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                return Tools.getDate(o1.getField("start_date"))
                        .compareTo(Tools.getDate(o2.getField("start_date")));
            }
        });
        if(workouts.length > 0) {
            last_wk = new Workout(workouts[workouts.length - 1]);
            try {
                lastWkTitle.setText(last_wk.getTitle() + " - " + Tools.dispDuration(last_wk.getDuration()) + " - " + Tools.dispDate(last_wk.getStartDate()));
            } catch (Exception e) {
                lastWkTitle.setVisibility(View.GONE);
            }
            try {
                lastWkDescription.setText(last_wk.getDescription());
            } catch (Exception e) {
                lastWkDescription.setVisibility(View.GONE);
            }
           /* try {
                lastWkDuration.setText(Tools.dispDuration(last_wk.getDuration()));
            } catch (Exception e) {
                lastWkDuration.setVisibility(View.GONE);
            }
            try {
                lastWkDate.setText(Tools.dispDate(last_wk.getStartDate()));
            } catch (Exception e) {
                lastWkDate.setVisibility(View.GONE);
            }*/
        } else {
            Main.toast("Pas de dernier entrainement");
        }
    }

    public void setTodayPlCard() {
        Document[] plans = MeteorSingleton.getInstance().getDatabase().getCollection("plans").whereEqual("owner", app.getTargetedUser().getUsername()).find();
        Arrays.sort(plans, new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                return Tools.getDate(o1.getField("monday_date"))
                        .compareTo(Tools.getDate(o2.getField("monday_date")));
            }
        });

        if(plans.length > 0) {
            today_pl = new Plan(plans[0]);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            if (c.getTime().getDay() == today_pl.getMondayDate().getDay() && c.getTime().getMonth() == today_pl.getMondayDate().getMonth() && c.getTime().getYear() == today_pl.getMondayDate().getYear()) {
                Calendar t = Calendar.getInstance();
                int index = t.get(Calendar.DAY_OF_WEEK);
                try {
                    todayPlDescription.setText(today_pl.getDays().get(index - 1).get("description"));
                    //todayPlDuration.setText(Tools.dispDuration(today_pl.getDays().get(index - 1).get("duration")));
                    todayPlType.setText(Tools.dispType(today_pl.getDays().get(index - 1).get("type")));
                    //todayPlSupport.setText(Tools.dispSupport(today_pl.getDays().get(index - 1).get("support")));
                } catch (Exception e) {}
            } else {
                todayPlDescription.setText("Pas de plan cette semaine");
                todayPlSupport.setVisibility(View.GONE);
                todayPlDuration.setVisibility(View.GONE);
                todayPlType.setVisibility(View.GONE);
            }
        } else {
            Main.toast("Pas de plan pour cette semaine");
        }
    }
}
