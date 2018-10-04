package net.dunrou.mobile.bean;

import java.io.Serializable;

public class EventPic implements Serializable {
    public int attachmentId;
    public String imageId;
    public String imageUrl;       // 原图
    public String smallImageUrl;  // 缩略图

    public EventPic(String image) {
        this.imageUrl = image;
        this.smallImageUrl = image;
    }

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }
}
