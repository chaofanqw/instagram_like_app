package net.dunrou.mobile.base.firebaseClass;

import android.location.Location;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseEventPost {
    private String eventPostId;
    private String userId;
    private String comment;
    private List<String> photos;
    private Location location;
    private Date time;

    public FirebaseEventPost(){}

    public FirebaseEventPost(String eventPostId, String userId, String comment, List<String> photos, Location location, Date time){
        this.eventPostId = eventPostId;
        this.userId = userId;
        this.comment = comment;
        this.photos = photos;
        this.location = location;
        this.time = time;
    }

    public FirebaseEventPost(String eventPostId, String userId, String comment, String photos, String location, String time){
        this.eventPostId = eventPostId;
        this.userId = userId;
        this.comment = comment;

        Gson gson = new Gson();
        Type typeOfT = TypeToken.getParameterized(List.class, String.class).getType();
        this.photos = gson.fromJson(photos,typeOfT);

        if(!location.equals("null")) {
            ArrayList<String> tempLocation = gson.fromJson(location, typeOfT);
            this.location = new Location("dummyprovider");
            this.location.setLatitude(Double.parseDouble(tempLocation.get(0)));
            this.location.setLongitude(Double.parseDouble(tempLocation.get(1)));
        }

        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        this.time = gson.fromJson(time, Date.class);
    }

    public Map<String, Object> toMap(){
        Gson gson = new Gson();
        HashMap<String, Object> result = new HashMap<>();
        result.put("eventPostId", eventPostId);
        result.put("userId", userId);
        result.put("comment", comment);
        result.put("photos", gson.toJson(photos));

        if(location == null){
            result.put("location", "null");
        }else {
            ArrayList<String> tempLocation = new ArrayList<>();
            tempLocation.add(String.valueOf(location.getLatitude()));
            tempLocation.add(String.valueOf(location.getLongitude()));
            result.put("location", gson.toJson(tempLocation));
        }

        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        result.put("time", gson.toJson(time));

        return result;
    }

    public String getEventPostId() {
        return eventPostId;
    }
    public void setEventPostId(String eventPostId) {
        this.eventPostId = eventPostId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public List<String> getPhotos() {
        return photos;
    }
    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

}
