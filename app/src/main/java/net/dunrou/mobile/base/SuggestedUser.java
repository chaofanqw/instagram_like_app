package net.dunrou.mobile.base;

import android.util.Log;

import net.dunrou.mobile.base.converter.UriConverter;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;

import java.net.URI;
import java.util.HashMap;

public class SuggestedUser extends FirebaseUser{
    private String description = " follower";
    private int avatarID = -1;
    private Boolean isFollowed = false;
    private int value = 0;
    private String avatarString = "";

    public SuggestedUser() {
        super();
    }

    public SuggestedUser(SuggestedUser suggestedUser) {
        super();
        super.setUserID(suggestedUser.getUserID());
        super.setAvatar(suggestedUser.getAvatar());
        super.setPassword(suggestedUser.getPassword());

        this.description = suggestedUser.description;
        this.avatarID = suggestedUser.avatarID;
        this.isFollowed = suggestedUser.isFollowed;
        this.value = suggestedUser.value;
        this.avatarString = suggestedUser.getAvatarString();
    }

    public SuggestedUser(FirebaseUser firebaseUser) {
        super.setUserID(firebaseUser.getUserID());
        super.setPassword(firebaseUser.getPassword());
        super.setAvatar(firebaseUser.getAvatar());
    }

    public SuggestedUser(String userID) {
        super();
        super.setUserID(userID);
    }

    public SuggestedUser(String userID, int avactarID) {
        super();
        super.setUserID(userID);
        this.avatarID = avactarID;
    }

    public SuggestedUser(String userID, Boolean isFollowed, int value) {
        super();
        super.setUserID(userID);
        this.isFollowed = isFollowed;
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public void setIsFollowed(Boolean isFollowed) {
        this.isFollowed = isFollowed;
    }

    public Boolean getIsFollowed() {
        return isFollowed;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


    public void fromMap(HashMap<String, Object> result){
        super.setUserID((String) result.get("userID"));
        super.setPassword((String) result.get("password"));
        if(result.get("avatar") != null) {
            super.setAvatar(URI.create((String) result.get("avatar")));
            this.avatarString = (String) result.get("avatar");
            Log.d("avatar", (String) result.get("avatar"));
        }
        this.value = 0;
    }

    public void setAvatarString(String avatarString) {
        this.avatarString = avatarString;
    }

    public String getAvatarString() {
        return avatarString;
    }

}
