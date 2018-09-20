package net.dunrou.mobile.base.message;

/**
 * Created by Stephen on 2018/9/21.
 */

public class LoginMessage {
    private boolean success;
    private String userID;

    public LoginMessage(boolean success, String userID){
        this.success = success;
        this.userID = userID;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
