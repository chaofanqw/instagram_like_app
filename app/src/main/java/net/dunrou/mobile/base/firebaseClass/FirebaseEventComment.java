package net.dunrou.mobile.base.firebaseClass;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseEventComment {
    private String eventCommentId;
    private String userId;
    private String eventPostId;
    private String comment;
    private Date time;

    public FirebaseEventComment() {
    }

    public FirebaseEventComment(String userId, String eventPostId,
                        String comment, Date time) {
        this.userId = userId;
        this.eventPostId = eventPostId;
        this.comment = comment;
        this.time = time;
    }

    public FirebaseEventComment(String eventCommentId, String userId, String eventPostId,
                                String comment, String time) {

        this.eventCommentId = eventCommentId;
        this.userId = userId;
        this.eventPostId = eventPostId;
        this.comment = comment;

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        this.time = gson.fromJson(time, Date.class);
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("eventCommentId", eventCommentId);
        result.put("userID", userId);
        result.put("eventPostId", eventPostId);
        result.put("comment", comment);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        result.put("time", gson.toJson(time));
        return result;
    }

    public void fromMap(HashMap<String, Object> result){
        this.eventCommentId = (String) result.get("eventCommentId");
        this.userId = (String) result.get("userID");
        this.eventPostId = (String) result.get("eventPostId");
        this.comment = (String) result.get("comment");

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        this.time = gson.fromJson((String) result.get("time"), Date.class);


    }

    public String getEventCommentId() {
        return eventCommentId;
    }
    public void setEventCommentId(String eventCommentId) {
        this.eventCommentId = eventCommentId;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getEventPostId() {
        return this.eventPostId;
    }
    public void setEventPostId(String eventPostId) {
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

}
