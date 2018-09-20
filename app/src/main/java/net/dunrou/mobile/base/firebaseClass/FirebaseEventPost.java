package net.dunrou.mobile.base.firebaseClass;

import android.location.Location;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseEventPost {
    private String userId;
    private String comment;
    private List<Uri> photos;
    private Location location;
    private Date time;

    public FirebaseEventPost(String userId, String comment, List<Uri> photos, Location location, Date time){
        this.userId = userId;
        this.comment = comment;
        this.photos = photos;
        this.location = location;
        this.time = time;
    }

    public FirebaseEventPost(String userId, String comment, String photos, String location, String time){
        Gson gson = new Gson();
        this.userId = userId;
        this.comment = comment;
        Type typeOfT = TypeToken.getParameterized(List.class, Uri.class).getType();
        this.photos = gson.fromJson(photos,typeOfT);
        this.location = gson.fromJson(location, Location.class);
        this.time = gson.fromJson(time, Date.class);
    }

    public FirebaseEventPost(){}


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Uri> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Uri> photos) {
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

    public Map<String, Object> toMap(){
        Gson gson = new Gson();
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("comment", comment);
        result.put("photos", gson.toJson(photos));
        result.put("location", gson.toJson(location));
        result.put("time", gson.toJson(time));

        return result;
    }
}
