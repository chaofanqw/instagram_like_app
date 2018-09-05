package net.dunrou.mobile.base;

import net.dunrou.mobile.base.converter.DateConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Stephen on 2018/9/5.
 */

@Entity(
        indexes = {
                @Index(value = "follower DESC, followee DESC", unique = true)
        })
public class Relationship {
    @Id(autoincrement = true)
    private Long relationshipId;
    private String follower;
    private String followee;
    @Convert(converter = DateConverter.class, columnType = String.class)
    private Date time;
    private Boolean status;
@Generated(hash = 632700789)
public Relationship(Long relationshipId, String follower, String followee,
        Date time, Boolean status) {
    this.relationshipId = relationshipId;
    this.follower = follower;
    this.followee = followee;
    this.time = time;
    this.status = status;
}
@Generated(hash = 1339996331)
public Relationship() {
}
public Long getRelationshipId() {
    return this.relationshipId;
}
public void setRelationshipId(Long relationshipId) {
    this.relationshipId = relationshipId;
}
public String getFollower() {
    return this.follower;
}
public void setFollower(String follower) {
    this.follower = follower;
}
public String getFollowee() {
    return this.followee;
}
public void setFollowee(String followee) {
    this.followee = followee;
}
public Date getTime() {
    return this.time;
}
public void setTime(Date time) {
    this.time = time;
}
public Boolean getStatus() {
    return this.status;
}
public void setStatus(Boolean status) {
    this.status = status;
}
}
