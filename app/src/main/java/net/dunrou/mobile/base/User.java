package net.dunrou.mobile.base;

import net.dunrou.mobile.base.converter.UriConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.net.URI;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Stephen on 2018/9/5.
 */

@Entity
public class User {
    @Id
    private String userID;
    private String password;
    @Convert(converter = UriConverter.class, columnType = String.class)
    private URI avatar;
    @Generated(hash = 634306988)
    public User(String userID, String password, URI avatar) {
        this.userID = userID;
        this.password = password;
        this.avatar = avatar;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public String getUserID() {
        return this.userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public URI getAvatar() {
        return this.avatar;
    }
    public void setAvatar(URI avatar) {
        this.avatar = avatar;
    }
}
