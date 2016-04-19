package io.somet.somet.data;

import java.lang.reflect.Field;
import java.util.HashMap;

import im.delight.android.ddp.db.Document;
import io.somet.somet.helpers.Tools;

public class User {
    private String id;
    private String username;
    private String completeName;
    private boolean trainer;
    private String email;
    private String myTrainer;
    private String[] myAthletes;
    private boolean stravaSynced;
    private float weight;
    private float height;

    public User(Document userDoc) {
        HashMap<String, Object> user = Tools.getMap(userDoc);
        HashMap<String, Object> profile = (HashMap<String, Object>) Tools.getObject(user, "profile", true);

        this.id = Tools.getString(user, "_id");
        this.username = Tools.getString(user, "username");
        this.completeName = Tools.getString(profile, "complete_name");
        this.trainer = Tools.getBoolean(profile, "trainer");
        this.email = Tools.getString(profile, "email");
        this.myTrainer = Tools.getString(profile, "my_trainer");
        this.myAthletes = Tools.getStringsArray(profile, "my_athletes");
        this.stravaSynced = Tools.getBoolean(profile, "strava_sync");
        this.weight = Tools.getFloat(profile, "weight");
        this.height = Tools.getFloat(profile, "height");
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public String getCompleteName() {
        return completeName;
    }

    public boolean isTrainer() {
        return trainer;
    }

    public String getEmail() {
        return email;
    }

    public String getMyTrainer() {
        return myTrainer;
    }

    public String[] getMyAthletes() {
        return myAthletes;
    }

    public boolean isStravaSynced() {
        return stravaSynced;
    }

    public float getWeight() {
        return weight;
    }

    public float getHeight() {
        return height;
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
