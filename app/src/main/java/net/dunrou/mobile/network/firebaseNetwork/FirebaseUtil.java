package net.dunrou.mobile.network.firebaseNetwork;

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

import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.base.firebaseClass.FirebaseRelationship;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;
import net.dunrou.mobile.base.message.DiscoverMessage;
import net.dunrou.mobile.base.message.LoginMessage;
import net.dunrou.mobile.base.message.UploadDatabaseMessage;

import org.greenrobot.eventbus.EventBus;

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

    public void EventPostInsert(FirebaseEventPost firebaseEventPost){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        String key = myRef.child("event-post").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();

        firebaseEventPost.setEventPostId(key);
        childUpdates.put("/event-post/"+key, firebaseEventPost.toMap());

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

    public void getEventPost(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("event-post");

        Query query = myRef.orderByChild("userId").equalTo("1");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();

                while (set.hasNext()){
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

    public void UserInsert(final FirebaseUser firebaseUser){

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        String key = myRef.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, firebaseUser.toMap());

        myRef.updateChildren( childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        EventBus.getDefault().post(new LoginMessage(true, firebaseUser.getUserID()));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        EventBus.getDefault().post(new LoginMessage(false,""));
                    }
                });
    }

    public void getUser(final FirebaseUser firebaseUser){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("user");

        Query query = myRef.orderByChild("userID").equalTo(firebaseUser.getUserID());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();

                if(set.hasNext()){
//                    Log.d("result", "onDataChange: ");
                    DataSnapshot tempDataSnapshot = set.next();
                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();

                    if(((String)result.get("password"))
                            .equals(firebaseUser.getPassword())){
                        EventBus.getDefault().post(new LoginMessage(true, firebaseUser.getUserID()));
                    }else{
                        EventBus.getDefault().post(new LoginMessage(false, ""));
                    }
                }else{
                    EventBus.getDefault().post(new LoginMessage(false, ""));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }

    public void updateRelationship(final FirebaseRelationship firebaseRelationship, Boolean isExisted){

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("relationship");

        Map<String, Object> childUpdates = new HashMap<>();
        if(isExisted)
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
//                        EventBus.getDefault().post(new DiscoverUserAdapter.RelationAddedEvent(firebaseRelationship));
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

        Query query  = myRef.orderByChild("follower");

        //TODO can not listen the whole relationship be deleted

//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();
////                Log.d(TAG, "setRelationshipsListener.onDataChange called");
//
//                while(set.hasNext()){
//                    DataSnapshot tempDataSnapshot = set.next();
//                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();
//                    FirebaseRelationship firebaseRelationship = new FirebaseRelationship();
//                    firebaseRelationship.fromMap(result);
//                    EventBus.getDefault().post(new DiscoverMessage.RelationshipAddedEvent(firebaseRelationship));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("result", "ValueEventListener onCancelled: error");
//            }
//        });

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

        Query query  = myRef.orderByChild("userID");

//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterator<DataSnapshot> set = dataSnapshot.getChildren().iterator();
//
//                while(set.hasNext()){
//                    DataSnapshot tempDataSnapshot = set.next();
//                    HashMap<String, Object> result = (HashMap<String, Object>) tempDataSnapshot.getValue();
//                    FirebaseUser firebaseUser = new FirebaseUser();
//                    firebaseUser.setUserID((String) result.get("userID"));
//                    EventBus.getDefault().post(new DiscoverMessage.UserAddedEvent(firebaseUser));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "ValueEventListener onCancelled: error");
//            }
//        });

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseUser firebaseUser = new FirebaseUser();
                firebaseUser.setUserID((String) result.get("userID"));
                EventBus.getDefault().post(new DiscoverMessage.UserAddedEvent(firebaseUser));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseUser firebaseUser = new FirebaseUser();
                firebaseUser.setUserID((String) result.get("userID"));
                EventBus.getDefault().post(new DiscoverMessage.UserChangedEvent(firebaseUser));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Object> result = (HashMap<String, Object>) dataSnapshot.getValue();
                FirebaseUser firebaseUser = new FirebaseUser();
                firebaseUser.setUserID((String) result.get("userID"));
                EventBus.getDefault().post(new DiscoverMessage.UserRemovedEvent(firebaseUser));
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
