package net.dunrou.mobile.database;

import android.content.Context;

import net.dunrou.mobile.base.DaoSession;
import net.dunrou.mobile.base.EventComment;
import net.dunrou.mobile.base.EventCommentDao;

import java.util.List;

/**
 * Created by Stephen on 2018/9/5.
 */

public class EventCommentUtil {
    private static volatile EventCommentUtil eventCommentUtil;
    private DatabaseManager databaseManager;

    private EventCommentUtil(Context context){
        databaseManager = DatabaseManager.getInstance(context);
    }

    public static EventCommentUtil getInstance(Context context){
        if(eventCommentUtil == null){
            synchronized (EventCommentUtil.class){
                if(eventCommentUtil == null){
                    eventCommentUtil = new EventCommentUtil(context);
                }
            }
        }
        return eventCommentUtil;
    }

    // Create DaoSession every time prevent local daoSession cache
    // Do not create a local daoSession!
    public void insertEventComment(EventComment eventComment){
        DaoSession daoSession = databaseManager.getDaoSession();
        EventCommentDao eventCommentDao = daoSession.getEventCommentDao();
        eventCommentDao.insert(eventComment);
    }

    public void deleteEventComment(Long eventCommentId){
        DaoSession daoSession = databaseManager.getDaoSession();
        EventCommentDao eventCommentDao = daoSession.getEventCommentDao();
        eventCommentDao.deleteByKey(eventCommentId);
    }

    // http://greenrobot.org/greendao/documentation/queries/
    // document: how to make a query
    public List<EventComment> queryEventComment(){
        DaoSession daoSession = databaseManager.getDaoSession();
        EventCommentDao eventCommentDao = daoSession.getEventCommentDao();
        return eventCommentDao.queryBuilder()
                                .orderAsc(EventCommentDao.Properties.EventCommentId)
                                .build()
                                .list();
    }

    public void updateEventComment(EventComment eventComment){
        DaoSession daoSession = databaseManager.getDaoSession();
        EventCommentDao eventCommentDao = daoSession.getEventCommentDao();
        eventCommentDao.update(eventComment);
    }
}
