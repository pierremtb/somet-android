package io.somet.somet;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.SubscribeListener;

public class AnalysisFragment extends Fragment {

    OnFragmentInteractionListener Main;

    public AnalysisFragment() {
    }

    public static AnalysisFragment newInstance() {
        AnalysisFragment fragment = new AnalysisFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String usr = (String) (Main.isTrainer() ? Main.getSelectedAthlete() : Main.getUser().get("username"));
        HashMap<String, Number> target = new HashMap<>();
        target.put("year", -1);
        target.put("month", -1);
        System.out.println(usr);
        MeteorSingleton.getInstance().subscribe("thisTargetWorkoutsOfUsr", new Object[]{target, usr}, new SubscribeListener() {
            @Override
            public void onSuccess() {
                Main.toast(MeteorSingleton.getInstance().getDatabase().getCollection("workouts").count() + "");
            }

            @Override
            public void onError(String error, String reason, String details) {
                Main.toast(error);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (Main != null) {
            Main.onFragmentInteraction(uri);
        }
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
        Main = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
        void toast(String str);

        HashMap<String, Object> getUser();

        void setText(Integer id, Object txt);

        void openWorkout(Object id);

        boolean isTrainer();

        String getSelectedAthlete();
    }
}
