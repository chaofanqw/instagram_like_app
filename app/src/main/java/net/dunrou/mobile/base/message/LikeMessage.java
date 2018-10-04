package net.dunrou.mobile.base.message;

public class LikeMessage {
    private int index;
    private String eventPostId;
    private String username;

    public LikeMessage(int index, String eventPostId, String username) {
        this.index = index;
        this.eventPostId = eventPostId;
        this.username = username;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getEventPostId() {
        return eventPostId;
    }

    public void setEventPostId(String eventPostId) {
        this.eventPostId = eventPostId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
