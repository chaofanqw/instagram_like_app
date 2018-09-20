package net.dunrou.mobile.base.firebaseClass;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseEventComment {
    private String userId;
    private Long eventPostId;
    private String comment;
    private Date time;

    public FirebaseEventComment(String userId, Long eventPostId,
                        String comment, Date time) {
        this.userId = userId;
        this.eventPostId = eventPostId;
        this.comment = comment;
        this.time = time;
    }

    public FirebaseEventComment() {
    }

    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Long getEventPostId() {
        return this.eventPostId;
    }
    public void setEventPostId(Long eventPostId) {
        this.eventPostId = eventPostId;
    }
    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Date getTime() {
        return this.time;
    }
    public void setTime(Date time) {
        this.time = time;
    }


    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userId);
        result.put("eventPostId", eventPostId);
        result.put("comment", comment);
        result.put("time", time);
        return result;
    }
}
