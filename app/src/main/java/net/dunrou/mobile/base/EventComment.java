package net.dunrou.mobile.base;

import net.dunrou.mobile.base.converter.DateConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Stephen on 2018/9/5.
 */

@Entity
public class EventComment {
    @Id(autoincrement = true)
    private Long eventCommentId;
    private String userId;
    private Long eventPostId;
    private String comment;
    @Convert(converter = DateConverter.class, columnType = String.class)
    private Date time;
    @Generated(hash = 1924958303)
    public EventComment(Long eventCommentId, String userId, Long eventPostId,
            String comment, Date time) {
        this.eventCommentId = eventCommentId;
        this.userId = userId;
        this.eventPostId = eventPostId;
        this.comment = comment;
        this.time = time;
    }
    @Generated(hash = 355508552)
    public EventComment() {
    }
    public Long getEventCommentId() {
        return this.eventCommentId;
    }
    public void setEventCommentId(Long eventCommentId) {
        this.eventCommentId = eventCommentId;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Long getEventPostId() {
        return this.eventPostId;
    }
    public void setEventPostId(Long eventPostId) {
        this.eventPostId = eventPostId;
    }
    public String getComment() {
        return this.comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Date getTime() {
        return this.time;
    }
    public void setTime(Date time) {
        this.time = time;
    }
}
