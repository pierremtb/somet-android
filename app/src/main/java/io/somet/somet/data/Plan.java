package io.somet.somet.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.db.Document;
import io.somet.somet.helpers.Tools;

public class Plan {
    private String id;
    private String owner;
    private String title;
    private String eventId;
    private Date mondayDate;
    private long totalDuration;
    private ArrayList<HashMap<String, String>> days;

    public Plan(Document planDoc) {
        HashMap<String, Object> pl = Tools.getMap(planDoc);
        this.id = String.valueOf(get(pl, "_id"));
        this.owner = String.valueOf(get(pl, "owner"));
        this.title = String.valueOf(get(pl, "title"));
        this.eventId = String.valueOf(get(pl, "eventId"));
        this.mondayDate = Tools.getDate(Tools.getObject(pl, "monday_date",true));
        this.totalDuration = Long.parseLong(String.valueOf(get(pl, "total_duration")));
        this.days = (ArrayList<HashMap<String, String>>) get(pl, "days");
    }

    private Object get(HashMap<String, ?> obj, String key) {
        return obj.containsKey(key) ? obj.get(key) : new Object();
    }

    private Object get(HashMap<String, ?> obj, String key, boolean json) {
        if(obj.containsKey(key)) {
            return obj.get(key);
        } else if(json) {
            return new HashMap<String, Object>();
        } else {
            return new Object();
        }
    }

    public String getEventId() {
        return eventId;
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public Date getMondayDate() {
        return mondayDate;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public ArrayList<HashMap<String, String>> getDays() {
        return days;
    }

    public static int getLastPlanId() {
        return lastPlanId;
    }

    private static int lastPlanId = 0;

    public static List<Plan> createPlansList(int numPlans, int offset, final String fieldSorting, final int sortingOrder) {
        List<Plan> Plans = new ArrayList<Plan>();

        Document[] workoutsDocs = MeteorSingleton.getInstance().getDatabase().getCollection("plans").find();


        System.out.println(fieldSorting);

        Arrays.sort(workoutsDocs, new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                switch (fieldSorting) {
                    case "start_date": {
                        Date o1v = (new Plan(o1)).getMondayDate(), o2v = (new Plan(o2).getMondayDate());
                        return sortingOrder == -1 ? o2v.compareTo(o1v) : o1v.compareTo(o2v);
                    }
                    case "title": {
                        String o1v = (new Plan(o1)).getTitle(), o2v = (new Plan(o2).getTitle());
                        return sortingOrder == -1 ? o1v.compareTo(o2v) : o2v.compareTo(o1v);
                    }
                    case "duration": {
                        Long o1v = (new Plan(o1)).getTotalDuration(), o2v = (new Plan(o2).getTotalDuration());
                        return sortingOrder == -1 ? o2v.compareTo(o1v) : o1v.compareTo(o2v);
                    }
                    default: {
                        Date o1v = (new Plan(o1)).getMondayDate(), o2v = (new Plan(o2).getMondayDate());
                        return sortingOrder == -1 ? o2v.compareTo(o1v) : o1v.compareTo(o2v);
                    }
                }
            }
        });

        if(numPlans < 0 || numPlans > workoutsDocs.length)
            numPlans = workoutsDocs.length;
        if(offset < 0 || offset > workoutsDocs.length)
            offset = 0;

        for (int i=offset; i<numPlans; i++){
            Plans.add(new Plan(workoutsDocs[i]));
        }

        return Plans;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);
        Field[] fields = this.getClass().getDeclaredFields();
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {  }
            result.append(newLine);
        }
        result.append("}");
        return result.toString();
    }
}