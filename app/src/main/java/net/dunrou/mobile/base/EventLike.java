package net.dunrou.mobile.base;

import net.dunrou.mobile.base.converter.DateConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import org.greenrobot.greendao.annotation.Generated;
import java.util.Date;

/**
 * Created by Stephen on 2018/9/5.
 */
@Entity
public class EventLike {
    @Id(autoincrement = true)
    private Long eventLikeId;
    private String eventPostId;
    private String userId;
    @Convert(converter = DateConverter.class, columnType = String.class)
    private Date time;
    private Boolean status;
    @Generated(hash = 227620941)
    public EventLike(Long eventLikeId, String eventPostId, String userId, Date time,
            Boolean status) {
        this.eventLikeId = eventLikeId;
        this.eventPostId = eventPostId;
        this.userId = userId;
        this.time = time;
        this.status = status;
    }
    @Generated(hash = 1723880409)
    public EventLike() {
    }
    public Long getEventLikeId() {
        return this.eventLikeId;
    }
    public void setEventLikeId(Long eventLikeId) {
        this.eventLikeId = eventLikeId;
    }
    public String getEventPostId() {
        return this.eventPostId;
    }
    public void setEventPostId(String eventPostId) {
        this.eventPostId = eventPostId;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public java.util.Date getTime() {
        return this.time;
    }
    public void setTime(java.util.Date time) {
        this.time = time;
    }
    public Boolean getStatus() {
        return this.status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }

}
