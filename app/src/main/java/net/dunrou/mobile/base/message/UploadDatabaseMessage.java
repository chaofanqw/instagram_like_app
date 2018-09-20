package net.dunrou.mobile.base.message;

/**
 * Created by Stephen on 2018/9/20.
 */

public class UploadDatabaseMessage {
    private boolean success;

    public UploadDatabaseMessage(boolean success){
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
