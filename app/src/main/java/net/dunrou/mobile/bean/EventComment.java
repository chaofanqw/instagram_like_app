package net.dunrou.mobile.bean;

import net.dunrou.mobile.base.firebaseClass.FirebaseEventComment;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class EventComment implements Serializable {
    public String feedCommentID;             //回复id
    public String feedCommentContent;    //回复内容
    public String feedCommentUsername;  //回复人姓名
    public String feedCommentTime;  //回复时间

    public EventComment(String feedCommentID, String feedCommentContent, String feedCommentUsername, String feedCommentTime) {
        this.feedCommentID = feedCommentID;
        this.feedCommentContent = feedCommentContent;
        this.feedCommentUsername = feedCommentUsername;
        this.feedCommentTime = feedCommentTime;
    }

    public EventComment(FirebaseEventComment comment) {
        this.feedCommentID = comment.getEventCommentId();
        this.feedCommentContent = comment.getComment();
        this.feedCommentUsername = comment.getUserId();
        this.feedCommentTime = new SimpleDateFormat("dd-MM, hh:mm").format(comment.getTime());
    }

    public String getFeedCommentID() {
        return feedCommentID;
    }

    public void setFeedCommentID(String feedCommentID) {
        this.feedCommentID = feedCommentID;
    }

    public String getFeedCommentContent() {
        return feedCommentContent;
    }

    public void setFeedCommentContent(String feedCommentContent) {
        this.feedCommentContent = feedCommentContent;
    }

    public String getFeedCommentUsername() {
        return feedCommentUsername;
    }

    public void setFeedCommentUsername(String feedCommentUsername) {
        this.feedCommentUsername = feedCommentUsername;
    }

    public String getFeedCommentTime() {
        return feedCommentTime;
    }

    public void setFeedCommentTime(String feedCommentTime) {
        this.feedCommentTime = feedCommentTime;
    }
}
