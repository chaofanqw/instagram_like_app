package net.dunrou.mobile.base.firebaseClass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseEventLike {
    private String eventLikeId;
    private String eventPostId;
    private String userId;
    private Date time;
    private Boolean status;

    public FirebaseEventLike(String eventPostId, String userId, Date time,
                     Boolean status) {
        this.eventPostId = eventPostId;
        this.userId = userId;
        this.time = time;
        this.status = status;
    }

    public FirebaseEventLike() {
    }

    public FirebaseEventLike(String eventLikeId, String eventPostId, String userId, String time,
                             String status) {
        this.eventLikeId = eventLikeId;
        this.eventPostId = eventPostId;
        this.userId = userId;

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        this.time = gson.fromJson(time, Date.class);

        this.status = Boolean.parseBoolean(status);
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("eventLikeId", eventLikeId);
        result.put("eventPostId", eventPostId);
        result.put("userId", userId);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        result.put("time", gson.toJson(time));

        result.put("status", String.valueOf(status));
        return result;
    }

    public String getEventLikeId() {
        return eventLikeId;
    }
    public void setEventLikeId(String eventLikeId) {
        this.eventLikeId = eventLikeId;
    }
    public String getEventPostId() {
        return this.eventPostId;
    }
    public void setEventPostId(String eventPostId) {
        this.eventPostId = eventPostId;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public java.util.Date getTime() {
        return this.time;
    }
    public void setTime(java.util.Date time) {
        this.time = time;
    }
    public Boolean getStatus() {
        return this.status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void fromMap(HashMap<String, Object> result) {
        this.eventPostId = (String) result.get("eventPostId");
        this.eventLikeId = (String) result.get("eventLikeId");
        this.userId = (String) result.get("userId");


        this.status = Boolean.valueOf((String)result.get("status"));


        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        this.time = gson.fromJson((String) result.get("time"), Date.class);


    }


}
