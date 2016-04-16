package io.somet.somet;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.SubscribeListener;
import im.delight.android.ddp.db.Document;

public class AnalysisFragment extends Fragment {

    OnFragmentInteractionListener Main;

    TextView summaryDuration, summaryDistance, summaryAscent, summaryDescent, summaryCalories;
    CardView summaryCard;

    public AnalysisFragment() {
    }

    public static AnalysisFragment newInstance() {
        AnalysisFragment fragment = new AnalysisFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        MainActivity.startLoadingDialog(view.getContext());
        summaryCard = (CardView) view.findViewById(R.id.summaryCard);
        summaryCard.setVisibility(View.GONE);
        summaryDuration = (TextView) view.findViewById(R.id.summaryDuration);
        summaryDistance = (TextView) view.findViewById(R.id.summaryDistance);
        summaryAscent = (TextView) view.findViewById(R.id.summaryAscent);
        summaryDescent = (TextView) view.findViewById(R.id.summaryDescent);
        summaryCalories = (TextView) view.findViewById(R.id.summaryCalories);
        setSummaryCard();

        return view;
    }

    public void setSummaryCard() {
        Document[] workouts = MeteorSingleton.getInstance().getDatabase().getCollection("workouts").whereEqual("owner", (String) (Main.isTrainer() ? Main.getSelectedAthlete() : Main.getUser().get("username"))).find();
        long sum_dur = 0;
        float sum_dis = 0, sum_asc = 0, sum_des = 0, sum_cal = 0;
        for(Document wk:workouts) {
            try {
                sum_dur += Long.valueOf(wk.getField("duration").toString());
            } catch (Exception e) {}
            try {
                sum_dis += Float.valueOf(wk.getField("distance").toString());
            } catch (Exception e) {}
            try {
                sum_asc += Float.valueOf(wk.getField("ascent").toString());
            } catch (Exception e) {}
            try {
                sum_des += Float.valueOf(wk.getField("descent").toString());
            } catch (Exception e) {}
            try {
                sum_cal += Float.valueOf(wk.getField("calories").toString());
            } catch (Exception e) {}
        }
        summaryDuration.setText(Tools.dispDuration(sum_dur));
        summaryDistance.setText(String.format("%.2fkm", sum_dis/1000));
        summaryAscent.setText(String.format("%.2fm", sum_asc));
        summaryDescent.setText(String.format("%.2fm", sum_des));
        summaryCalories.setText(String.format("%.2fkcal", sum_cal));
        MainActivity.dismissLoadingDialog();
        summaryCard.setVisibility(View.VISIBLE);
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
        void toast(String str);

        HashMap<String, Object> getUser();

        void setText(Integer id, Object txt);

        void openWorkout(Object id);

        boolean isTrainer();

        String getSelectedAthlete();
    }
}
