package net.dunrou.mobile.bean;

import net.dunrou.mobile.base.firebaseClass.FirebaseEventComment;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EventItem implements Serializable {
    public String eventPostId;                      // 评论id
    public List<EventPic> attachments;       // 评论图片列表
    public Avatar avatar;
    public String content;                  // 内容信息
    public String createTime;               // 评论时间
    public String userId;
    public List<EventComment> feedReplies;  //回复列表内容
    public int likeCount;
    public boolean selfLike;

    public EventItem(FirebaseEventPost eventPost) {
        this.eventPostId = eventPost.getEventPostId();
        this.attachments = new ArrayList<>();
        for (String image : eventPost.getPhotos()) {
            attachments.add(new EventPic(image));
        }
        //this.avatar = avatar;
        this.content = eventPost.getComment();
        this.createTime = new SimpleDateFormat("dd-MM, hh:mm").format(eventPost.getTime());
        this.userId = eventPost.getUserId();
        //this.feedReplies = feedReplies;
    }

    public EventItem(String avatar, FirebaseEventPost eventPost, int likeCount, ArrayList<FirebaseEventComment> postReply, boolean selfLike) {
        if (!avatar.equals("0")) {
            this.avatar = new Avatar(avatar);
        }
        this.eventPostId = eventPost.getEventPostId();
        this.attachments = new ArrayList<>();
        for (String image : eventPost.getPhotos()) {
            attachments.add(new EventPic(image));
        }
        if (!eventPost.getComment().trim().equals("")) {
            this.content = eventPost.getComment();
        }
        this.createTime = new SimpleDateFormat("dd-MM, hh:mm").format(eventPost.getTime());
        this.userId = eventPost.getUserId();
        this.likeCount = likeCount;
        this.selfLike = selfLike;
        if (postReply.size() != 0) {
            this.feedReplies = new ArrayList<>();
            for (FirebaseEventComment comment : postReply) {
                feedReplies.add(new EventComment(comment));
            }
        }
    }

    public List<EventPic> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<EventPic> attachments) {
        this.attachments = attachments;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEventPostId() {
        return eventPostId;
    }

    public void setEventPostId(String eventPostId) {
        this.eventPostId = eventPostId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<EventComment> getFeedReplies() {
        return feedReplies;
    }

    public void setFeedReplies(List<EventComment> feedReplies) {
        this.feedReplies = feedReplies;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isSelfLike() {
        return selfLike;
    }

    public void setSelfLike(boolean selfLike) {
        this.selfLike = selfLike;
    }
}
