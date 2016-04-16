package io.somet.somet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import im.delight.android.ddp.MongoDb;
import im.delight.android.ddp.Protocol;
import im.delight.android.ddp.db.Document;

public final class Tools{

    public static HashMap<String, Object> getMap(Document doc) {
        HashMap<String, Object> obj = new HashMap<>();
        if(doc != null) {
            for (String f : doc.getFieldNames()) {
                obj.put(f, doc.getField(f));
            }
            obj.put("_id", doc.getId());
        }
        return obj;
    }

    public static HashMap<String, Object> getMap(Object doc) {
        HashMap<String, Object> obj = new HashMap<>();
        obj = (HashMap<String, Object>) doc;
        return obj;
    }

    public static String dispDuration(long sec) {
        return String.format("%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, sec % 60);
    }

    public static String dispDuration(Object str) {
        int sec = 0;
        try{
            sec = Integer.parseInt(str.toString());
        } catch (Exception e) {
            return "";
        }
        return String.format("%02d:%02d:%02d", sec / 3600, (sec % 3600) / 60, sec % 60);
    }

    public static String dispDate(Object str) {
        HashMap<String, Long> t = (HashMap<String, Long>) str;
        long timestamp = 0;
        try {
            timestamp = t.get("$date");
        } catch (Exception e ) {}
        Date date = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public static String dispDate(Date date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    public static Date getDate(Object dateObj) {
        HashMap<String, Long> t = (HashMap<String, Long>) dateObj;
        long timestamp = 0;
        try {
            timestamp = t.get("$date");
        } catch (Exception e ) {}
        return new Date(timestamp);
    }
}