package io.somet.somet;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.HashMap;
import java.util.StringTokenizer;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.SubscribeListener;
import im.delight.android.ddp.db.Document;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    OnFragmentInteractionListener Main;

    HashMap<String, Object> last_wk = new HashMap<>();

    TextView wkDetailsButton;

    public DashboardFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String usr = (String) Main.getUser().get("username");
        System.out.println(usr);
        MeteorSingleton.getInstance().subscribe("lastWorkoutOfUsrSync", new Object[]{ usr }, new SubscribeListener() {
            @Override
            public void onSuccess() {
                setLastWkCard();
                //progressDialog.dismiss();
            }

            @Override
            public void onError(String error, String reason, String details) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        wkDetailsButton = (TextView) myView.findViewById(R.id.lastWkDetails);
        wkDetailsButton.setOnClickListener(this);
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
                Main.openWorkout(last_wk.get("_id"));
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void toast(String str);
        HashMap<String, Object> getUser();
        void setText(Integer id, Object txt);
        void openWorkout(Object id);
    }

    public void setLastWkCard() {
        last_wk = MainActivity.getMap(MeteorSingleton.getInstance().getDatabase().getCollection("workouts").findOne());
        System.out.println(last_wk);
        Main.setText(R.id.lastWkTitle, last_wk.get("title"));
        Main.setText(R.id.lastWkDescription, last_wk.get("description"));
        Main.setText(R.id.lastWkDuration, MainActivity.dispDuration(last_wk.get("duration")));
        Main.setText(R.id.lastWkDate, MainActivity.dispDate(last_wk.get("start_date")));
    }
}
