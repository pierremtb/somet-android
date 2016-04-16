package io.somet.somet.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import im.delight.android.ddp.db.Document;
import io.somet.somet.helpers.Tools;

public class Plan {
    private String id;
    private String owner;
    private String title;
    private Date mondayDate;
    private long totalDuration;
    private ArrayList<HashMap<String, String>> days;

    public Plan(Document planDoc) {
        HashMap<String, Object> pl = Tools.getMap(planDoc);
        this.id = String.valueOf(get(pl, "_id"));
        this.owner = String.valueOf(get(pl, "owner"));
        this.title = String.valueOf(get(pl, "title"));
        this.mondayDate = Tools.getDate(get(pl, "monday_date"));
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

    public static List<Plan> createPlansList(int numPlans, int offset) {
        List<Plan> Plans = new ArrayList<Plan>();

        for (int i = 1; i <= numPlans; i++) {
            //Plans.add(new Plan("Person " + ++lastPlanId + " offset: " + offset, i <= numPlans / 2));
        }

        return Plans;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( this.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                result.append( field.get(this) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }
}