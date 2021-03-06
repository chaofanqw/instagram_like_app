package net.dunrou.mobile.network.firebaseNetwork;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.dunrou.mobile.base.SuggestedUser;
import net.dunrou.mobile.base.converter.UriConverter;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventLike;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventComment;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventLike;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.base.firebaseClass.FirebaseRelationship;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;
import net.dunrou.mobile.base.message.ActivityFeedMessage;
import net.dunrou.mobile.base.message.DiscoverMessage;
import net.dunrou.mobile.base.message.CommentMessage;
import net.dunrou.mobile.base.message.LikeMessage;
import net.dunrou.mobile.base.message.LoginMessage;
import net.dunrou.mobile.base.message.ProfileMessage;
import net.dunrou.mobile.base.message.RefreshMessage;
import net.dunrou.mobile.base.message.UploadDatabaseMessage;
import net.dunrou.mobile.bean.EventItem;

import org.greenrobot.eventbus.EventBus;

import java.net.URI;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseUtil {
    private final static String TAG = FirebaseUtil.class.getSimpleName();

    FirebaseDatabase database;
    DatabaseReference myRef;

    private volatile int count;
    private volatile int postCount;
    private AtomicInteger avatarCount;
    private AtomicInteger likeCount;
    private AtomicInteger commentCount;
    private final Object postLock = new Object();
    private ArrayList<FirebaseEventPost> eventPostList = new ArrayList<>();
    private HashSet<String> usernameList = new HashSet<>();
    private ConcurrentHashMap<String, String> userAvatar = new ConcurrentHashMap<>();
    private int[] likeNum;
    private ArrayList[] postReplies;
    private boolean[] selfLike;
    private ArrayList[] followeeLike;
    private float[] distances;
    private ArrayList<EventItem> eventItems = new ArrayList<>();
    private volatile boolean canGet = true;

    public void EventPostInsert(FirebaseEventPost firebaseEventPost) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        String key = myRef.child("event-post").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();

        firebaseEventPost.setEventPostId(key);
        childUpdates.put("/event-post/" + key, firebaseEventPost.toMap());

        myRef.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("message", "onSuccess: ");
                        EventBus.getDefault().post(new UploadDatabaseMessage(true));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("message", "onFailure: ");
                        EventBus.getDefault().post(new UploadDatabaseMessage(false));
                    }
                });
    }

    public void getEventPost(final String username, final boolean isTime, final Location location) {
        if (canGet) {
            canGet = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("relationship");
                    eventPostList.clear();
                    usernameList.clear();
                    userAvatar.clear();
                    eventItems.clear();
                    count = 1;

                    Query query = myRef.orderByChild("follower").equalTo(username);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();
                            usernameList.add(username);
                            String name;
                            while (set.hasNext()) {
                                DataSnapshot tempDataSnapshot = set.next();
                                HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();
                                name = (String) result.get("followee");
                                if (result.get("status").equals("true")) {
                                    usernameList.add(name);
                                }
                            }
                            count = 0;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("result", "onCancelled: error");
                        }
                    });
                    while (count != 0);
                    postCount = usernameList.size();
                    avatarCount = new AtomicInteger(postCount);
                    for (String user : usernameList) {
                        getAvatar(user);
                        getPost(user);
                    }
                    while (postCount != 0);
                    if (isTime || (location == null)) {
                        Collections.sort(eventPostList, new Comparator<FirebaseEventPost>() {
                            @Override
                            public int compare(FirebaseEventPost eventPost1, FirebaseEventPost eventPost2) {
                                if (eventPost1.getTime().before(eventPost2.getTime())) {
                                    return 1;
                                } else if (eventPost1.getTime().after(eventPost2.getTime())) {
                                    return -1;
                                }
                                return 0;
                            }
                        });
                    } else {
                        Collections.sort(eventPostList, new Comparator<FirebaseEventPost>() {
                            @Override
                            public int compare(FirebaseEventPost eventPost1, FirebaseEventPost eventPost2) {
                                if (eventPost1.getLocation() != null && eventPost2.getLocation() != null) {
                                    float distance1 = location.distanceTo(eventPost1.getLocation());
                                    float distance2 = location.distanceTo(eventPost2.getLocation());
                                    if (distance1 > distance2) {
                                        return 1;
                                    } else if (distance1 < distance2) {
                                        return -1;
                                    } else {
                                        if (eventPost1.getTime().before(eventPost2.getTime())) {
                                            return 1;
                                        } else if (eventPost1.getTime().after(eventPost2.getTime())) {
                                            return -1;
                                        }
                                        return 0;
                                    }
                                } else if (eventPost1.getLocation() != null) {
                                    return -1;
                                } else if (eventPost2.getLocation() != null) {
                                    return 1;
                                } else {
                                    if (eventPost1.getTime().before(eventPost2.getTime())) {
                                        return 1;
                                    } else if (eventPost1.getTime().after(eventPost2.getTime())) {
                                        return -1;
                                    }
                                    return 0;
                                }
                            }
                        });
                    }
                    int size = eventPostList.size();
                    likeCount = new AtomicInteger(size);
                    commentCount = new AtomicInteger(size);
                    int num = 0;
                    likeNum = new int[size];
                    postReplies = new ArrayList[size];
                    selfLike = new boolean[size];
                    followeeLike = new ArrayList[size];
                    distances = new float[size];
                    for (FirebaseEventPost post : eventPostList) {
                        getLike(num, post.getEventPostId(), username);
                        getComment(num, post.getEventPostId());
                        num++;
                    }
                    EventItem eventItem;
                    FirebaseEventPost firebaseEventPost;
                    while (avatarCount.get() != 0 || likeCount.get() != 0 || commentCount.get() != 0);
                    for (int i = 0; i < size; i++) {
                        firebaseEventPost = eventPostList.get(i);
                        distances[i] = -1;
                        if (location != null && firebaseEventPost.getLocation() != null) {
                            distances[i] = location.distanceTo(firebaseEventPost.getLocation());
                        }
                        eventItem = new EventItem(userAvatar.get(firebaseEventPost.getUserId()), firebaseEventPost, likeNum[i], postReplies[i], selfLike[i], followeeLike[i], distances[i]);
                        eventItems.add(eventItem);
                    }
                    EventBus.getDefault().post(new RefreshMessage(eventItems));
                    canGet = true;
                }
            }).start();
        }
    }

    private void getPost(final String username) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("event-post");

        Query query = myRef.orderByChild("userId").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();
                List<FirebaseEventPost> postList = new ArrayList<>();
                while (set.hasNext()) {
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();

                    FirebaseEventPost data = new FirebaseEventPost(
                            (String) result.get("eventPostId"),
                            (String) result.get("userId"),
                            (String) result.get("comment"),
                            (String) result.get("photos"),
                            (String) result.get("location"),
                            (String) result.get("time"));
                    postList.add(data);
                    Log.d("result", "onDataChange: " + data.getComment());
                }
                synchronized (postLock) {
                    eventPostList.addAll(postList);
                    postCount--;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    private void getAvatar(final String username) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        Query query = myRef.orderByChild("userID").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();

                if (set.hasNext()) {
                    Log.d("result", "onDataChange: ");
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();

                    if (result != null && result.containsKey("avatar")) {
                        userAvatar.put(username, (String) result.get("avatar"));
                    } else {
                        userAvatar.put(username, "0");
                    }
                }
                avatarCount.decrementAndGet();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    private void getLike(final int i, final String eventPostId, final String username) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("relationship");
        final HashMap<String, Integer> followee = new HashMap<>();
        count = 1;
        Query query = myRef.orderByChild("follower").equalTo(username);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();
                String name;
                while (set.hasNext()) {
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();
                    name = (String) result.get("followee");
                    if (result.get("status").equals("true")) {
                        followee.put(name, 0);
                    }
                }
                count = 0;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
        while (count != 0);
        myRef = database.getReference("like");
        query = myRef.orderByChild("eventPostId").equalTo(eventPostId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();
                likeNum[i] = 0;
                int selfLikeNum = 0;
                selfLike[i] = false;

                while (set.hasNext()) {
                    Log.d("result", "onDataChange: ");
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();

                    if (result != null) {
                        if (result.containsKey("status")) {
                            if (result.get("status").equals("true")) {
                                likeNum[i]++;
                                if (result.containsKey("userId")) {
                                    String name = (String) result.get("userId");
                                    if (name.equals(username)) {
                                        selfLikeNum++;
                                    }
                                    if (followee.containsKey(name)) {
                                        followee.put(name, followee.get(name) + 1);
                                    }
                                }
                            } else {
                                likeNum[i]--;
                                if (result.containsKey("userId")) {
                                    String name = (String) result.get("userId");
                                    if (name.equals(username)) {
                                        selfLikeNum--;
                                    }
                                    if (followee.containsKey(name)) {
                                        followee.put(name, followee.get(name) - 1);
                                    }
                                }
                            }
                        }
                    }
                }
                if (selfLikeNum > 0) {
                    selfLike[i] = true;
                }
                followeeLike[i] = new ArrayList<String>();
                for (Map.Entry<String, Integer> entry : followee.entrySet()) {
                    if (entry.getValue() > 0) {
                        followeeLike[i].add(entry.getKey());
                    }
                }
                likeCount.decrementAndGet();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    private void getComment(final int i, final String eventPostId) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("comment");

        Query query = myRef.orderByChild("eventPostId").equalTo(eventPostId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();
                postReplies[i] = new ArrayList<FirebaseEventComment>();
                FirebaseEventComment eventComment;

                while (set.hasNext()) {
                    Log.d("result", "onDataChange: ");
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();

                    eventComment = new FirebaseEventComment(
                            (String) result.get("eventCommentId"),
                            (String) result.get("userID"),
                            (String) result.get("eventPostId"),
                            (String) result.get("comment"),
                            (String) result.get("time"));

                    postReplies[i].add(eventComment);
                }
                Collections.sort(postReplies[i], new Comparator<FirebaseEventComment>() {
                    @Override
                    public int compare(FirebaseEventComment eventPost1, FirebaseEventComment eventPost2) {
                        if (eventPost1.getTime().before(eventPost2.getTime())) {
                            return 1;
                        } else if (eventPost1.getTime().after(eventPost2.getTime())) {
                            return -1;
                        }
                        return 0;
                    }
                });
                commentCount.decrementAndGet();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    public void insertLike(final FirebaseEventLike eventLike, final int index) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference();

                String key = myRef.child("like").push().getKey();
                Map<String, Object> childUpdates = new HashMap<>();

                eventLike.setEventLikeId(key);
                childUpdates.put("/like/" + key, eventLike.toMap());

                myRef.updateChildren(childUpdates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("message", "onSuccess: ");
                                EventBus.getDefault().post(new LikeMessage(index, eventLike.getEventPostId(), eventLike.getUserId()));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("message", "onFailure: ");
                            }
                        });
            }
        }).start();
    }

    public void refreshLike(final LikeMessage likeMessage) {
        if (canGet) {
            canGet = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    likeCount = new AtomicInteger(1);
                    getLike(likeMessage.getIndex(), likeMessage.getEventPostId(), likeMessage.getUsername());
                    EventItem eventItem;
                    FirebaseEventPost firebaseEventPost;
                    eventItems.clear();
                    while (likeCount.get() != 0);
                    for (int i = 0; i < eventPostList.size(); i++) {
                        firebaseEventPost = eventPostList.get(i);
                        eventItem = new EventItem(userAvatar.get(firebaseEventPost.getUserId()), firebaseEventPost, likeNum[i], postReplies[i], selfLike[i], followeeLike[i], distances[i]);
                        eventItems.add(eventItem);
                    }
                    EventBus.getDefault().post(new RefreshMessage(eventItems));
                    canGet = true;
                }
            }).start();
        }
    }

    public void insertComment(final FirebaseEventComment eventComment, final int index) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference();

                String key = myRef.child("comment").push().getKey();
                Map<String, Object> childUpdates = new HashMap<>();

                eventComment.setEventCommentId(key);
                childUpdates.put("/comment/" + key, eventComment.toMap());

                myRef.updateChildren(childUpdates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("message", "onSuccess: ");
                                EventBus.getDefault().post(new CommentMessage(index, eventComment.getEventPostId(), eventComment.getUserId()));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("message", "onFailure: ");
                            }
                        });
            }
        }).start();
    }

    public void refreshComment(final CommentMessage commentMessage) {
        if (canGet) {
            canGet = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    commentCount = new AtomicInteger(1);
                    getComment(commentMessage.getIndex(), commentMessage.getEventPostId());
                    EventItem eventItem;
                    FirebaseEventPost firebaseEventPost;
                    eventItems.clear();
                    while (commentCount.get() != 0);
                    for (int i = 0; i < eventPostList.size(); i++) {
                        firebaseEventPost = eventPostList.get(i);
                        eventItem = new EventItem(userAvatar.get(firebaseEventPost.getUserId()), firebaseEventPost, likeNum[i], postReplies[i], selfLike[i], followeeLike[i], distances[i]);
                        eventItems.add(eventItem);
                    }
                    EventBus.getDefault().post(new RefreshMessage(eventItems));
                    canGet = true;
                }
            }).start();
        }
    }

    public void UserInsert(final FirebaseUser firebaseUser) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        Query query = myRef.orderByChild("userID").equalTo(firebaseUser.getUserID());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();
                if (set.hasNext()) {
                    EventBus.getDefault().post(new LoginMessage(false, firebaseUser.getUserID(), 0));
                } else {
                    String key = myRef.push().getKey();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/" + key, firebaseUser.toMap());

                    myRef.updateChildren(childUpdates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    EventBus.getDefault().post(new LoginMessage(true, firebaseUser.getUserID(), 0));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    EventBus.getDefault().post(new LoginMessage(false, "", 0));
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    public void getUser(final FirebaseUser firebaseUser) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        Query query = myRef.orderByChild("userID").equalTo(firebaseUser.getUserID());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();

                if (set.hasNext()) {
                    Log.d("result", "onDataChange: ");
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();

                    if (((String) result.get("password"))
                            .equals(firebaseUser.getPassword())) {
                        EventBus.getDefault().post(new LoginMessage(true, firebaseUser.getUserID(), 1));
                    } else {
                        EventBus.getDefault().post(new LoginMessage(false, "", 1));
                    }
                } else {
                    EventBus.getDefault().post(new LoginMessage(false, "", 1));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    public void updateRelationship(final FirebaseRelationship firebaseRelationship, Boolean isExisted) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("relationship");

        Map<String, Object> childUpdates = new HashMap<>();
        if (isExisted)
            childUpdates.put("/" + firebaseRelationship.getRelationshipId(), firebaseRelationship.toMap());
        else {
            String key = myRef.push().getKey();
            firebaseRelationship.setRelationshipId(key);
            childUpdates.put("/" + key, firebaseRelationship.toMap());
        }

        myRef.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        EventBus.getDefault().post(new DiscoverMessage.RelationAddFailEvent());
                    }
                });
    }

    public void setRelationshipsListener() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("relationship");

        Query query = myRef.orderByChild("follower");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseRelationship firebaseRelationship = new FirebaseRelationship();
                firebaseRelationship.fromMap(result);
                Log.d(TAG, "setRelationshipsListener.onChildAdded value: " +
                        firebaseRelationship.getFollowee() + " " + firebaseRelationship.getStatus());
                EventBus.getDefault().post(new DiscoverMessage.RelationshipAddedEvent(firebaseRelationship));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseRelationship firebaseRelationship = new FirebaseRelationship();
                firebaseRelationship.fromMap(result);
                Log.d(TAG, "setRelationshipsListener.onChildChanged value: " +
                        firebaseRelationship.getFollowee() + " " + firebaseRelationship.getStatus());
                EventBus.getDefault().post(new DiscoverMessage.RelationshipChangedEvent(firebaseRelationship));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseRelationship firebaseRelationship = new FirebaseRelationship();
                firebaseRelationship.fromMap(result);
                EventBus.getDefault().post(new DiscoverMessage.RelationshipRemovedEvent(firebaseRelationship));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "ChildEventListener onCancelled: error");
            }
        });
    }

    public void setUsersListener() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        Query query = myRef.orderByChild("userID");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                SuggestedUser user = new SuggestedUser();
                user.fromMap(result);
                EventBus.getDefault().post(new DiscoverMessage.UserAddedEvent(user));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                SuggestedUser user = new SuggestedUser();
                user.fromMap(result);
                EventBus.getDefault().post(new DiscoverMessage.UserChangedEvent(user));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                SuggestedUser user = new SuggestedUser();
                user.fromMap(result);
                EventBus.getDefault().post(new DiscoverMessage.UserRemovedEvent(user));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "ChildEventListener onCancelled: error");
            }
        });
    }

    public void searchUser(final String userID_part) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        Query query = myRef.orderByChild("userID").startAt(userID_part);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();
                ArrayList<SuggestedUser> searchUsers = new ArrayList<>();

                while (set.hasNext()) {
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();
                    SuggestedUser searchUser = new SuggestedUser();
                    searchUser.fromMap(result);
                    searchUsers.add(searchUser);
                    Log.d(TAG, "searchUser: " + searchUser.getUserID());
                }
                EventBus.getDefault().post(new DiscoverMessage.UserSearchGetEvent(searchUsers));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserProfile(final FirebaseUser firebaseUser) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        Query query = myRef.orderByChild("userID").equalTo(firebaseUser.getUserID());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();

                if (set.hasNext()) {
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();

                    FirebaseUser user = new FirebaseUser();
                    user.fromMap(result);

                    EventBus.getDefault().post(new ProfileMessage.RefreshProfile(user));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    public void getProfileStats(final FirebaseUser user) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("relationship");

        Query query = myRef.orderByChild("status").equalTo("true");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();

                int numFollowers = 0;
                int numFollowing = 0;

                while (set.hasNext()) {
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();

                    if (((String) result.get("follower")).equals(user.getUserID())) {
                        numFollowing++;
                    }
                    if (((String) result.get("followee")).equals(user.getUserID())) {
                        numFollowers++;
                    }
                }
                EventBus.getDefault().post(new ProfileMessage.UpdateNumOfFollowers(numFollowers));
                EventBus.getDefault().post(new ProfileMessage.UpdateNumOfFollowing(numFollowing));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    public void getPostStats(final FirebaseUser user){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("event-post");

        Query query = myRef.orderByChild("userId").equalTo(user.getUserID());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();

                int posts = 0;

                while (set.hasNext()) {
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();

                    posts++;
                    Log.d("PostResult", String.valueOf(posts));
                }
                EventBus.getDefault().post(new ProfileMessage.UpdateNumOfPosts(posts));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    public void getAllUsersData(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        Query query = myRef.orderByChild("userID");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseUser user = new FirebaseUser();
                user.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.UserAddedEvent(user));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseUser user = new FirebaseUser();
                user.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.UserChangedEvent(user));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseUser user = new FirebaseUser();
                user.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.UserRemovedEvent(user));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "ChildEventListener onCancelled: error");
            }
        });
    }

    public void getAllRelationshipData(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("relationship");

        Query query = myRef.orderByChild("time");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseRelationship firebaseRelationship = new FirebaseRelationship();
                firebaseRelationship.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.RelationshipAddedEvent(firebaseRelationship));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseRelationship firebaseRelationship = new FirebaseRelationship();
                firebaseRelationship.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.RelationshipChangedEvent(firebaseRelationship));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseRelationship firebaseRelationship = new FirebaseRelationship();
                firebaseRelationship.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.RelationshipRemovedEvent(firebaseRelationship));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "ChildEventListener onCancelled: error");
            }
        });
    }

    public void getLikeData(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("like");

        Query query = myRef.orderByChild("time");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventLike like = new FirebaseEventLike();
                like.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.LikeAddedEvent(like));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventLike like = new FirebaseEventLike();
                like.fromMap(result);

                EventBus.getDefault().post(new ActivityFeedMessage.LikeChangedEvent(like));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventLike like = new FirebaseEventLike();
                like.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.LikeRemovedEvent(like));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "ChildEventListener onCancelled: error");
            }
        });

    }

    public void getAllMyPostsForProfile(String user){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("event-post");

        Query query = myRef.orderByChild("userId").equalTo(user);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventPost post = new FirebaseEventPost();
                post.fromMap(result);
                EventBus.getDefault().post(new ProfileMessage.PostAddedEvent(post));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventPost post = new FirebaseEventPost();
                post.fromMap(result);

                EventBus.getDefault().post(new ProfileMessage.PostChangedEvent(post));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventPost post = new FirebaseEventPost();
                post.fromMap(result);
                EventBus.getDefault().post(new ProfileMessage.PostRemovedEvent(post));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "ChildEventListener onCancelled: error");
            }
        });
    }

    public void getAllMyPostsForActivityFeed(String user){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("event-post");

        Query query = myRef.orderByChild("userId").equalTo(user);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventPost post = new FirebaseEventPost();
                post.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.PostAddedEvent(post));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventPost post = new FirebaseEventPost();
                post.fromMap(result);

                EventBus.getDefault().post(new ActivityFeedMessage.PostChangedEvent(post));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventPost post = new FirebaseEventPost();
                post.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.PostRemovedEvent(post));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "ChildEventListener onCancelled: error");
            }
        });
    }

    public void getAllCommentsForActivityFeed(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("comment");

        Query query = myRef.orderByChild("userId");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventComment comment = new FirebaseEventComment();
                comment.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.CommentAddedEvent(comment));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventComment comment = new FirebaseEventComment();
                comment.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.CommentChangedEvent(comment));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventComment comment = new FirebaseEventComment();
                comment.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.CommentRemovedEvent(comment));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "ChildEventListener onCancelled: error");
            }
        });
    }

    public void getAllPostsForActivityFeed(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("event-post");

        Query query = myRef.orderByChild("userId");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventPost post = new FirebaseEventPost();
                post.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.AllPostAddedEvent(post));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventPost post = new FirebaseEventPost();
                post.fromMap(result);

                EventBus.getDefault().post(new ActivityFeedMessage.AllPostChangedEvent(post));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseEventPost post = new FirebaseEventPost();
                post.fromMap(result);
                EventBus.getDefault().post(new ActivityFeedMessage.AllPostRemovedEvent(post));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "ChildEventListener onCancelled: error");
            }
        });
    }


}
