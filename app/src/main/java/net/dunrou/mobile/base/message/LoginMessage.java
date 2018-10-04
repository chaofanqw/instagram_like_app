package net.dunrou.mobile.base.message;

/**
 * Created by Stephen on 2018/9/21.
 */

public class LoginMessage {
    private boolean success;
    private String userID;
    private int status;

    public LoginMessage(boolean success, String userID, int status){
        this.success = success;
        this.userID = userID;
        this.status = status;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
