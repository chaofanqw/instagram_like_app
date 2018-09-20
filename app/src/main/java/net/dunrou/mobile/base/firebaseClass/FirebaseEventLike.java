package net.dunrou.mobile.base.firebaseClass;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseEventLike {
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

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("eventPostId", eventPostId);
        result.put("userId", userId);
        result.put("time", time);
        result.put("status", status);

        return result;
    }
}
