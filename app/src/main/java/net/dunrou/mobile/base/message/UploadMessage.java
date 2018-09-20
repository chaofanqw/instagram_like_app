package net.dunrou.mobile.base.message;

import android.net.Uri;

/**
 * Created by Stephen on 2018/9/19.
 */

public class UploadMessage {
    private boolean success;
    private Uri path;

    public UploadMessage(boolean success, Uri path){
        this.success = success;
        this.path = path;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }
}
