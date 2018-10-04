package net.dunrou.mobile.base.message;

import net.dunrou.mobile.bean.EventItem;

import java.util.ArrayList;

public class RefreshMessage {
    private ArrayList<EventItem> eventItems;

    public RefreshMessage(ArrayList<EventItem> eventItems) {
        this.eventItems = eventItems;
    }

    public ArrayList<EventItem> getEventItems() {
        return eventItems;
    }

    public void setEventItems(ArrayList<EventItem> eventItems) {
        this.eventItems = eventItems;
    }
}
