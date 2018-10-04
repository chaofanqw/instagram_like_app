package net.dunrou.mobile.bean;

import java.io.Serializable;

public class Avatar implements Serializable {
    public String mangoId;
    public String picUrl;
    public String smallPicUrl;

    public Avatar(String url) {
        this.picUrl = url;
        this.smallPicUrl = url;
    }

    public String getMangoId() {
        return mangoId;
    }

    public void setMangoId(String mangoId) {
        this.mangoId = mangoId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getSmallPicUrl() {
        return smallPicUrl;
    }

    public void setSmallPicUrl(String smallPicUrl) {
        this.smallPicUrl = smallPicUrl;
    }
}
