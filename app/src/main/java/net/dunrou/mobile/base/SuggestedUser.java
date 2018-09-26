package net.dunrou.mobile.base;

public class SuggestedUser extends User {
    private String description = " followers";
    private int avatarID = -1;
    private Boolean isFollowed = false;
    private int value = 0;

    public SuggestedUser() {
        super();
    }

    public SuggestedUser(SuggestedUser suggestedUser) {
        super();
        super.setUserID(suggestedUser.getUserID());
        super.setAvatar(suggestedUser.getAvatar());
        super.setPassword(suggestedUser.getPassword());

        this.description = suggestedUser.description;
        this.avatarID = suggestedUser.avatarID;
        this.isFollowed = suggestedUser.isFollowed;
        this.value = suggestedUser.value;
    }

    public SuggestedUser(String userID) {
        super();
        super.setUserID(userID);
    }

    public SuggestedUser(String userID, int avactarID) {
        super();
        super.setUserID(userID);
        this.avatarID = avactarID;
    }

    public SuggestedUser(String userID, Boolean isFollowed, int value) {
        super();
        super.setUserID(userID);
        this.isFollowed = isFollowed;
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public void setIsFollowed(Boolean isFollowed) {
        this.isFollowed = isFollowed;
    }

    public Boolean getIsFollowed() {
        return isFollowed;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
