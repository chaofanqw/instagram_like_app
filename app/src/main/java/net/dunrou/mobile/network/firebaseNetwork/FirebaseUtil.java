package net.dunrou.mobile.network.firebaseNetwork;

import android.net.Uri;
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
import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.base.firebaseClass.FirebaseRelationship;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;
import net.dunrou.mobile.base.message.ActivityFeedMessage;
import net.dunrou.mobile.base.message.DiscoverMessage;
import net.dunrou.mobile.base.message.LoginMessage;
import net.dunrou.mobile.base.message.ProfileMessage;
import net.dunrou.mobile.base.message.UploadDatabaseMessage;

import org.greenrobot.eventbus.EventBus;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseUtil {
    private final static String TAG = FirebaseUtil.class.getSimpleName();

    FirebaseDatabase database;
    DatabaseReference myRef;

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

    public void getEventPost() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("event-post");

        Query query = myRef.orderByChild("userId").equalTo("1");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();

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

//                    Log.d("result", "onDataChange: "+data.getComment());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    public void UserInsert(final FirebaseUser firebaseUser) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        String key = myRef.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, firebaseUser.toMap());

        myRef.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        EventBus.getDefault().post(new LoginMessage(true, firebaseUser.getUserID()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        EventBus.getDefault().post(new LoginMessage(false, ""));
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
//                    Log.d("result", "onDataChange: ");
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();

                    if (((String) result.get("password"))
                            .equals(firebaseUser.getPassword())) {
                        EventBus.getDefault().post(new LoginMessage(true, firebaseUser.getUserID()));
                    } else {
                        EventBus.getDefault().post(new LoginMessage(false, ""));
                    }
                } else {
                    EventBus.getDefault().post(new LoginMessage(false, ""));
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


}
