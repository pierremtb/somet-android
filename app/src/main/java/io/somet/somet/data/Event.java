package io.somet.somet.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.db.Document;
import io.somet.somet.helpers.Tools;

public class Event {

    private String id;
    private Date date;
    private String description;
    private String title;
    private int eventClass;

    public Event(Document eventDoc) {
        HashMap<String, Object> event = Tools.getMap(eventDoc);

        this.id = Tools.getString(event, "_id");
        this.title = Tools.getString(event, "title");
        this.description = Tools.getString(event, "description");
        this.date = Tools.getDate(event, "date");
        this.eventClass = Tools.getBoolean(event, "first_class_event") ? 1 : Tools.getBoolean(event, "second_class_event") ? 2 : 3;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getEventClass() {
        return eventClass;
    }

    public static List<Event> createEventsList() {
        List<Event> Events = new ArrayList<Event>();

        Document[] eventsDocs = MeteorSingleton.getInstance().getDatabase().getCollection("events").find();

        for (Document ev:eventsDocs) {
            Events.add(new Event(ev));
        }

        return Events;
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
