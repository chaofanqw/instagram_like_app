package net.dunrou.mobile.network.firebaseNetwork;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.base.message.UploadDatabaseMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stephen on 2018/9/20.
 */

public class FirebaseUtil {
    FirebaseDatabase database;
    DatabaseReference myRef;

    private void init(){
        database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference();
    }

    public void EventPostInsert(FirebaseEventPost firebaseEventPost){
        init();
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


}
