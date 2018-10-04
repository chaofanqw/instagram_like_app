package net.dunrou.mobile.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
    public int totalCount;   // 总评论数
    public int pageNo;       // 页号
    public int pageCount;
    public ArrayList<EventItem> eventItems;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public ArrayList<EventItem> getEvaluataions() {
        return eventItems;
    }

    public void setEvaluataions(ArrayList<EventItem> eventItems) {
        this.eventItems = eventItems;
    }

    @Override
    public String toString() {
        return "Event{" +
                "totalCount=" + totalCount +
                ", pageNo=" + pageNo +
                ", pageCount=" + pageCount +
                ", eventItems=" + eventItems +
                '}';
    }
}
