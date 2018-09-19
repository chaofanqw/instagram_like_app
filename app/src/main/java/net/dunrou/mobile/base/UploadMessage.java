package net.dunrou.mobile.base;

import net.dunrou.mobile.network.UploadImage;

/**
 * Created by Stephen on 2018/9/19.
 */

public class UploadMessage {
    private boolean success;
    private String path;

    public UploadMessage(boolean success, String path){
        this.success = success;
        this.path = path;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
