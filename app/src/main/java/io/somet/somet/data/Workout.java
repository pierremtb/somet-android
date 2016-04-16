package io.somet.somet.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.db.Document;
import io.somet.somet.helpers.Tools;

public class Workout {
    private String id;
    private float ascent;
    private int crEffort;
    private int crMood;
    private int crPleasure;
    private int crSensations;
    private String description;
    private String comments;
    private double distance;
    private long duration;
    private boolean fitLinked;
    private double fitCadenceAvg;
    private double fitCadenceMax;
    private int[] fitCadenceValues;
    private long fitCalories;
    private double fitDistanceTotal;
    private double[] fitDistanceValues;
    private double fitElevationAscent;
    private double fitElevationDescent;
    private double[] fitElevationValues;
    private double fitHeartRateAvg;
    private double fitHeartRateMax;
    private int[] fitHeartRateValues;
    private double fitPowerAvg;
    private double fitPowerMax;
    private int[] fitPowerValues;
    private HashMap<Long, Float> fitPowerPpr;
    private double fitSpeedAvg;
    private double fitSpeedMax;
    private double[] fitSpeedValues;
    private String fitSport;
    private long fitDuration;
    private Date fitStartDate;
    private long[] fitTimeValues;
    private String owner;
    private Date startDate;
    private long stravaId;
    private String support;
    private String title;

    public Workout(Document workoutDoc) {
        HashMap<String, Object> wk = Tools.getMap(workoutDoc);
        HashMap<String, Object> fit = (HashMap<String, Object>) get(wk, "fit_values", true);
        System.out.println(wk);
        System.out.println(fit);
        HashMap<String, Object> time = (HashMap<String, Object>) get(fit, "time", true);
        HashMap<String, Object> distance = (HashMap<String, Object>) get(fit, "distance", true);
        HashMap<String, Object> power = (HashMap<String, Object>) get(fit, "power", true);
        HashMap<String, Object> heart_rate = (HashMap<String, Object>) get(fit, "heart_rate", true);
        HashMap<String, Object> elevation = (HashMap<String, Object>) get(fit, "elevation", true);
        HashMap<String, Object> speed = (HashMap<String, Object>) get(fit, "speed", true);
        HashMap<String, Object> cadence = (HashMap<String, Object>) get(fit, "cadence", true);
        HashMap<String, Object> cr = (HashMap<String, Object>) get(wk, "cr", true);

        this.id = (String) get(wk, "_id");
        this.ascent = Float.valueOf(get(wk, "ascent").toString());
        this.distance = Double.parseDouble(get(wk, "distance") + "");
        this.duration = Long.parseLong(String.valueOf(get(wk, "duration")));
        this.description = String.valueOf(get(wk, "description").toString());
        this.comments = String.valueOf(get(wk, "comments"));
        this.owner = String.valueOf(get(wk, "owner"));
        this.startDate = Tools.getDate(get(wk, "start_date"));
        this.title = (String) get(wk, "title");
        this.stravaId = Long.parseLong(String.valueOf(get(wk, "strava_id")));
        this.support = (String) get(wk, "support");

        if(cr.size() > 0) {
            this.crEffort = Integer.valueOf(get(cr, "effort").toString());
            this.crMood = Integer.valueOf(get(cr, "mood").toString());
            this.crPleasure = Integer.valueOf(get(cr, "pleasure").toString());
            this.crSensations = Integer.valueOf(get(cr, "sensations").toString());
        }

        this.fitLinked = Boolean.getBoolean(get(wk, "fit_linked").toString());

        if(fit.size() > 0) {
            this.fitSport = String.valueOf(get(fit, "sport"));
            this.fitCalories = Long.valueOf(get(fit, "calories").toString());

            if (time.size() > 0) {
                this.fitTimeValues = (long[]) get(time, "values");
                this.fitDuration = Long.valueOf(get(time, "duration").toString());
                this.fitStartDate = Tools.getDate(get(time, "start_date"));
            }

            if (cadence.size() > 0) {
                this.fitCadenceAvg = (double) get(cadence, "avg");
                this.fitCadenceMax = (double) get(cadence, "max");
                this.fitCadenceValues = (int[]) get(cadence, "values");
            }

            if (distance.size() > 0) {
                this.fitDistanceTotal = (double) get(distance, "total");
                this.fitDistanceValues = (double[]) get(distance, "values");
            }

            if (elevation.size() > 0) {
                this.fitElevationAscent = (double) get(elevation, "ascent");
                this.fitElevationDescent = (double) get(elevation, "descent");
                this.fitElevationValues = (double[]) get(elevation, "values");
            }

            if (heart_rate.size() > 0) {
                this.fitHeartRateAvg = (double) get(heart_rate, "avg");
                this.fitHeartRateMax = (double) get(heart_rate, "max");
                this.fitHeartRateValues = (int[]) get(heart_rate, "values");
            }

            if (power.size() > 0) {
                this.fitPowerAvg = (double) get(power, "avg");
                this.fitPowerMax = (double) get(power, "max");
                this.fitPowerValues = (int[]) get(power, "values");
                this.fitPowerPpr = (HashMap<Long, Float>) get(power, "ppr");
            }

            if(speed.size() > 0) {
                this.fitSpeedAvg = (double) get(speed, "avg");
                this.fitSpeedMax = (double) get(speed, "max");
                this.fitSpeedValues = (double[]) get(speed, "values");
            }
        } else {
            this.fitLinked = false;
        }
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

    public float getAscent() {
        return ascent;
    }

    public int getCrEffort() {
        return crEffort;
    }

    public int getCrMood() {
        return crMood;
    }

    public int getCrPleasure() {
        return crPleasure;
    }

    public int getCrSensations() {
        return crSensations;
    }

    public String getDescription() {
        return description;
    }

    public String getComments() {
        return comments;
    }

    public double getDistance() {
        return distance;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isFitLinked() {
        return fitLinked;
    }

    public double getFitCadenceAvg() {
        return fitCadenceAvg;
    }

    public double getFitCadenceMax() {
        return fitCadenceMax;
    }

    public int[] getFitCadenceValues() {
        return fitCadenceValues;
    }

    public long getFitCalories() {
        return fitCalories;
    }

    public double getFitDistanceTotal() {
        return fitDistanceTotal;
    }

    public double[] getFitDistanceValues() {
        return fitDistanceValues;
    }

    public double getFitElevationAscent() {
        return fitElevationAscent;
    }

    public double getFitElevationDescent() {
        return fitElevationDescent;
    }

    public double[] getFitElevationValues() {
        return fitElevationValues;
    }

    public double getFitHeartRateAvg() {
        return fitHeartRateAvg;
    }

    public double getFitHeartRateMax() {
        return fitHeartRateMax;
    }

    public int[] getFitHeartRateValues() {
        return fitHeartRateValues;
    }

    public double getFitPowerAvg() {
        return fitPowerAvg;
    }

    public double getFitPowerMax() {
        return fitPowerMax;
    }

    public int[] getFitPowerValues() {
        return fitPowerValues;
    }

    public HashMap<Long, Float> getFitPowerPpr() {
        return fitPowerPpr;
    }

    public double getFitSpeedAvg() {
        return fitSpeedAvg;
    }

    public double getFitSpeedMax() {
        return fitSpeedMax;
    }

    public double[] getFitSpeedValues() {
        return fitSpeedValues;
    }

    public String getFitSport() {
        return fitSport;
    }

    public long getFitDuration() {
        return fitDuration;
    }

    public Date getFitStartDate() {
        return fitStartDate;
    }

    public long[] getFitTimeValues() {
        return fitTimeValues;
    }

    public String getOwner() {
        return owner;
    }

    public Date getStartDate() {
        return startDate;
    }

    public long getStravaId() {
        return stravaId;
    }

    public String getSupport() {
        return support;
    }

    public String getTitle() {
        return title;
    }

    private static int lastWorkoutId = 0;

    public static List<Workout> createWorkoutsList(int numWorkouts, int offset) {
        List<Workout> Workouts = new ArrayList<Workout>();

        Document[] workoutsDocs = MeteorSingleton.getInstance().getDatabase().getCollection("workouts").find(numWorkouts, offset);

        for (Document wk:workoutsDocs) {
            Workouts.add(new Workout(wk));
        }

        System.out.println(Workouts.get(0));
        return Workouts;
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