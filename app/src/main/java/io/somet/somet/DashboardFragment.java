package io.somet.somet;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.StringTokenizer;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.SubscribeListener;
import im.delight.android.ddp.db.Document;

public class DashboardFragment extends Fragment {

    OnFragmentInteractionListener Main;

    public DashboardFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Main.toast((String) Main.getUser().get("username"));
        /*
        MeteorSingleton.getInstance().subscribe("getUserData", new Object[]{}, new SubscribeListener() {
            @Override
            public void onSuccess() {
                Document user_doc = MeteorSingleton.getInstance().getDatabase().getCollection("users").getDocument(MeteorSingleton.getInstance().getUserId());
                Main.toast("singletoneyy @" + user_doc.getField("username") + ".");
            }

            @Override
            public void onError(String error, String reason, String details) {

            }
        });*/

        String usr = (String) Main.getUser().get("username");
        System.out.println(usr);
        MeteorSingleton.getInstance().subscribe("getUserData", new Object[]{}, new SubscribeListener() {
            @Override
            public void onSuccess() {
                Integer wk = MeteorSingleton.getInstance().getDatabase().getCollection("users").count();
                System.out.println(wk);
                /*try{
                    Main.toast("singletoneyy @" + wk.getField("title") + ".");
                } catch (Exception e) {}*/
            }

            @Override
            public void onError(String error, String reason, String details) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
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

    public interface OnFragmentInteractionListener {
        void toast(String str);
        HashMap<String, Object> getUser();
    }
}
