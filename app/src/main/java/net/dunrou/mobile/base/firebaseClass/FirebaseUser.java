package net.dunrou.mobile.base.firebaseClass;

import android.util.Log;

import net.dunrou.mobile.base.converter.UriConverter;

import java.net.URI;
import java.util.HashMap;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseUser {
    private String userID;
    private String password;
    private URI avatar = new UriConverter().convertToEntityProperty("");

    public FirebaseUser() {}

    public FirebaseUser(String userID, String password){
        this.userID = userID;
        this.password = password;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("password", password);

        return result;
    }

    public void setUserID(String userID){
        this.userID =userID;
    }
    public String getUserID() {
        return userID;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public void setAvatar(URI avatar) {
        this.avatar = avatar;
    }
    public URI getAvatar() {
        return avatar;
    }



    public void fromMap(HashMap<String, Object> result){
        this.userID = ((String) result.get("userID"));
        this.password = ((String) result.get("password"));
        if(result.get("avatar") != null) {
            this.avatar = (URI.create((String) result.get("avatar")));

            Log.d("avatar", (String) result.get("avatar"));
        }

    }

}
