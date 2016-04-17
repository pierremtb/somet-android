package io.somet.somet.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.db.Document;
import io.somet.somet.R;
import io.somet.somet.Somet;
import io.somet.somet.adapters.EventsAdapter;
import io.somet.somet.adapters.WorkoutsAdapter;
import io.somet.somet.data.Event;
import io.somet.somet.data.Workout;
import io.somet.somet.helpers.Tools;

public class CalendarFragment extends Fragment implements OnDateSelectedListener {

    Somet app;

    private OnFragmentInteractionListener Main;

    MaterialCalendarView widget;

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (Somet) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        widget = (MaterialCalendarView) view.findViewById(R.id.calendarView);

        Collection<CalendarDay> firstClassEvents = new ArrayList<>();
        Collection<CalendarDay> secondClassEvents = new ArrayList<>();
        Collection<CalendarDay> preparationEvents = new ArrayList<>();

        Document[] eventsDocs = MeteorSingleton.getInstance().getDatabase().getCollection("events").whereEqual("owner", app.getTargetedUser().getUsername()).find();

        Calendar cal = Calendar.getInstance();

        for(Document eventDoc:eventsDocs) {
            Event event = new Event(eventDoc);
            cal.setTime(event.getDate());
            CalendarDay day = new CalendarDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            switch (event.getEventClass()) {
                case 1:
                    firstClassEvents.add(day);
                    break;
                case 2:
                    secondClassEvents.add(day);
                    break;
                default:
                    preparationEvents.add(day);
                    break;
            }
        }

        widget.addDecorators(
                new EventDecorator(getResources().getColor(R.color.colorAccent), firstClassEvents),
                new EventDecorator(getResources().getColor(R.color.colorAccent), secondClassEvents),
                new EventDecorator(getResources().getColor(R.color.colorAccent), preparationEvents),
                new TodayDecorator()
                );

        widget.setOnDateChangedListener(this);

        /*
        RecyclerView rvItems = (RecyclerView) view.findViewById(R.id.rvEvents);
        final List<Event> allEvents = Event.createEventsList();
        final EventsAdapter adapter = new EventsAdapter(allEvents);
        rvItems.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        rvItems.setLayoutManager(linearLayoutManager);
        */

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            Main = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Main = null;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        Document[] eventsDocs = MeteorSingleton.getInstance().getDatabase().getCollection("events").find();

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        ArrayList<Event> selectedEvent = new ArrayList<Event>();
        boolean eventFound = false;


        for(Document eventDoc:eventsDocs) {
            Event event = new Event(eventDoc);
            if(fmt.format(date.getDate()).equals(fmt.format(event.getDate()))) {
                eventFound = true;
                selectedEvent.add(event);
            }
        }

        if(eventFound) {
            new MaterialDialog.Builder(getContext())
                    .title(selectedEvent.get(0).getTitle())
                    .content(Tools.dispDate(selectedEvent.get(0).getDate()) + "\n" +
                            selectedEvent.get(0).getDescription() + "\n" +
                            "Classe d'événement : " + selectedEvent.get(0).getEventClass())
                    .negativeText(R.string.close)
                    .positiveText(R.string.edit)
                    .theme(Theme.LIGHT)
                    .show();
        } else {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.addEvent)
                    .content(R.string.notYetAvailable)
                    .negativeText(R.string.close)
                    .theme(Theme.LIGHT)
                    .show();
        }
    }

    public interface OnFragmentInteractionListener {
        void toast(String str);

        void setText(Integer id, Object txt);

        void openWorkout(Object id);

        void openWorkouts();
    }

    public class EventDecorator implements DayViewDecorator {

        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
        }
    }

    public class TodayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public TodayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.semitransparent_white)));
        }

        public void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }
}
