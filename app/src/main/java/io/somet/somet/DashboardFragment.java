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
import java.util.Comparator;
import java.util.HashMap;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.SubscribeListener;
import im.delight.android.ddp.db.Document;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    OnFragmentInteractionListener Main;

    HashMap<String, Object> last_wk = new HashMap<>();

    TextView wkDetailsButton, lastWkTitle, lastWkDescription, lastWkDuration, lastWkDate;

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

        lastWkTitle = (TextView) myView.findViewById(R.id.lastWkTitle);
        lastWkDescription = (TextView) myView.findViewById(R.id.lastWkDescription);
        lastWkDuration = (TextView) myView.findViewById(R.id.lastWkDuration);
        lastWkDate = (TextView) myView.findViewById(R.id.lastWkDate);

        String usr = (String) (Main.isTrainer() ? Main.getSelectedAthlete() : Main.getUser().get("username"));
        System.out.println(usr);
        setLastWkCard();

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
        switch (v.getId()) {
            case R.id.lastWkDetails:
                System.out.println(last_wk.get("title"));
                Main.openWorkout(last_wk.get("_id"));
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void toast(String str);

        HashMap<String, Object> getUser();

        void setText(Integer id, Object txt);

        void openWorkout(Object id);

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
        last_wk = Tools.getMap(workouts[workouts.length - 1]);
        System.out.println(last_wk);
        try {
            lastWkTitle.setText(last_wk.get("title").toString());
        } catch (Exception e) {
            lastWkTitle.setVisibility(View.GONE);
        }
        try {
            lastWkDescription.setText(last_wk.get("description").toString());
        } catch (Exception e) {
            lastWkDescription.setVisibility(View.GONE);
        }
        try {
            lastWkDuration.setText(Tools.dispDuration(last_wk.get("duration")));
        } catch (Exception e) {
            lastWkDuration.setVisibility(View.GONE);
        }
        try {
            lastWkDate.setText(Tools.dispDate(last_wk.get("start_date")));
        } catch (Exception e) {
            lastWkDate.setVisibility(View.GONE);
        }
    }
}
