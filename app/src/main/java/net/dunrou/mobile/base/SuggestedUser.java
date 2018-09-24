package net.dunrou.mobile.base;

public class SuggestedUser extends User {
    private String description;
    private int avatarID = -1;
    private Boolean isFollowed = false;

    public SuggestedUser(String userID) {
        super();
        super.setUserID(userID);
    }

    public SuggestedUser(String userID, int avactarID) {
        super();
        super.setUserID(userID);
        this.avatarID = avactarID;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public void setFollowed(Boolean followed) {
        isFollowed = followed;
    }

    public Boolean getIsFollowed() {
        return isFollowed;
    }
}
