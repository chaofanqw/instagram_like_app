package net.dunrou.mobile.network.firebaseNetwork;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.base.message.UploadDatabaseMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseUtil {
    FirebaseDatabase database;
    DatabaseReference myRef;

    public void EventPostInsert(FirebaseEventPost firebaseEventPost){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        String key = myRef.child("event-post").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/event-post/"+key, firebaseEventPost.toMap());
        myRef.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Write was successful!
                        // ...
                        Log.d("message", "onSuccess: ");
                        EventBus.getDefault().post(new UploadDatabaseMessage(true));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                        Log.d("message", "onFailure: ");
                        EventBus.getDefault().post(new UploadDatabaseMessage(false));
                    }
                });;
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
                            (String) result.get("userId"),
                            (String) result.get("comment"),
                            (String) result.get("photos"),
                            (String) result.get("location"),
                            (String) result.get("time"));

                    Log.d("result", "onDataChange: "+data.getComment());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("result", "onCancelled: error");
            }
        });
    }



}
