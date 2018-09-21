package net.dunrou.mobile.network.firebaseNetwork;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.dunrou.mobile.base.message.UploadMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Stephen on 2018/9/11.
 */

public class UploadImage {
    FirebaseStorage storage;
    StorageReference storageReference;

    public void init(){
        this.storage = FirebaseStorage.getInstance();
        this.storageReference = this.storage.getReference();
    }

    public void upload(Uri file) {
        init();

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/png")
                .build();

        // Upload file and metadata to the path 'images/mountains.jpg'
        final StorageReference pathReference = storageReference.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = pathReference.putFile(file, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
        //                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
        //                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
                EventBus.getDefault().post(new UploadMessage(false, null));
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                // ...
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Log.d("firebase", "onSuccess: "+uri);
                        EventBus.getDefault().post(new UploadMessage(true, uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.d("firebase", "onFailure: error");
                        EventBus.getDefault().post(new UploadMessage(false, null));

                    }
                });
            }
        });
    }
}
