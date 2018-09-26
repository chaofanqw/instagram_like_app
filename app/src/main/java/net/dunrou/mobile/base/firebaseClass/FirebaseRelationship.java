package net.dunrou.mobile.base.firebaseClass;

import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseRelationship {
    private String relationshipId;
    private String follower;
    private String followee;
    private Date time;
    private Boolean status;

    public FirebaseRelationship() {
    }

    public FirebaseRelationship(String relationshipId, String follower, String followee,
                                Date time, Boolean status) {
        this.relationshipId = relationshipId;
        this.follower = follower;
        this.followee = followee;
        this.time = time;
        this.status = status;
    }

    public FirebaseRelationship(String relationshipId, String follower, String followee, String time, String status){
        this.relationshipId = relationshipId;
        this.follower = follower;
        this.followee = followee;

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        this.time = gson.fromJson(time, Date.class);
        this.status = Boolean.parseBoolean(status);
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();

        result.put("relationshipId", relationshipId);
        result.put("follower", follower);
        result.put("followee", followee);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        result.put("time", gson.toJson(time));

        result.put("status", String.valueOf(status));
        return result;
    }

    public void fromMap(HashMap<String, Object> result){
        this.relationshipId = (String) result.get("relationshipId");
        this.follower = (String) result.get("follower");
        this.followee = (String) result.get("followee");

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        this.time = gson.fromJson((String) result.get("time"), Date.class);

        this.status = Boolean.valueOf((String)result.get("status"));

        result.put("relationshipId", relationshipId);
        result.put("follower", follower);
        result.put("followee", followee);
    }

    public String getFollower() {
        return this.follower;
    }
    public void setFollower(String follower) {
        this.follower = follower;
    }
    public String getFollowee() {
        return this.followee;
    }
    public void setFollowee(String followee) {
        this.followee = followee;
    }
    public Date getTime() {
        return this.time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
    public Boolean getStatus() {
        return this.status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
    public String getRelationshipId() {
        return relationshipId;
    }
    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

}

