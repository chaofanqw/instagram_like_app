package net.dunrou.mobile.bean;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.dunrou.mobile.R;
import net.dunrou.mobile.activity.MainActivity;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventComment;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventLike;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.base.firebaseClass.FirebaseRelationship;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ActivityFeedAdapter extends RecyclerView.Adapter<ActivityFeedAdapter.ActivityFeedViewHolder> {

    private final static String TAG = ActivityFeedAdapter.class.getSimpleName();

    private Context adapterContext;
    private String currentUser;

    // Modes
    public static final int FOLLOW_ME = 1;
    public static final int LIKE_ME = 2;
    public static final int ACTIVITIES = 3;
    public static final int COMMENTS = 4;
    public int currentMode = FOLLOW_ME;

    // Raw Data
    private ArrayList<FirebaseUser> allUsers;
    private ArrayList<FirebaseRelationship> allRelationships;
    private ArrayList<FirebaseEventLike> allLikes;
    private ArrayList<FirebaseEventPost> allMyPosts;
    private ArrayList<FirebaseEventComment> showComments;
    private ArrayList<FirebaseEventPost> allPosts;


    // Real time Data
    private ArrayList<FirebaseRelationship> allFollowMe;
    private ArrayList<FirebaseEventLike> allLikeMe;
    private ArrayList<FirebaseRelationship> followingActivities;
    private ArrayList<FirebaseEventComment> myComments;


    public ActivityFeedAdapter(Context context) {
        adapterContext = context;
        currentUser = MainActivity.CURRENT_USERID;

        allUsers = new ArrayList<>();
        allRelationships = new ArrayList<>();
        allLikes = new ArrayList<>();
        allMyPosts = new ArrayList<>();
        allPosts = new ArrayList<>();
        showComments = new ArrayList<>();

        allFollowMe = new ArrayList<>();
        allLikeMe = new ArrayList<>();
        followingActivities = new ArrayList<>();
        myComments = new ArrayList<>();


        new FirebaseUtil().getAllUsersData();
        new FirebaseUtil().getAllRelationshipData();
        new FirebaseUtil().getAllMyPostsForActivityFeed(currentUser);
        new FirebaseUtil().getLikeData();
        new FirebaseUtil().getAllCommentsForActivityFeed();
        new FirebaseUtil().getAllPostsForActivityFeed();

    }

    @Override
    public ActivityFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ActivityFeedViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.following_item, null));
    }

    @Override
    public void onBindViewHolder(ActivityFeedViewHolder holder, int position) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();

        switch (currentMode) {
            case FOLLOW_ME:
                Log.d("ActivityFeedAdapter", "Change to Follow me.");


                FirebaseRelationship tempFollow = allFollowMe.get(getItemCount() - 1 - position);
                holder.followerName.setText(tempFollow.getFollower());
                holder.doing.setText("starts following");
                holder.followeeName.setText("You");
                holder.time.setText(dateFormat.format(tempFollow.getTime()));


                String uriFollower = getPhotoURI(tempFollow.getFollower());
                if (uriFollower.equals("")) {
                    holder.followerPhoto.setImageResource(R.drawable.profile_p);
                } else {
                    Picasso.with(adapterContext).load(uriFollower).fit().into(holder.followerPhoto);
                }


                String uriFollowee = getPhotoURI(tempFollow.getFollowee());

                if (uriFollowee.equals("")) {
                    holder.followeePhoto.setImageResource(R.drawable.profile_p);
                } else {
                    Picasso.with(adapterContext).load(uriFollowee).fit().into(holder.followeePhoto);
                }

                holder.photoLike.setVisibility(View.GONE);
                holder.followeeName.setVisibility(View.VISIBLE);
                holder.followeePhoto.setVisibility(View.VISIBLE);
                holder.comment.setVisibility(View.GONE);
                break;


            case LIKE_ME:
                Log.d("ActivityFeedAdapter", "Change to Like me.");
                holder.followeeName.setVisibility(View.GONE);
                FirebaseEventLike tempLike = allLikeMe.get(getItemCount() - 1 - position);

                String uriUser = getPhotoURI(tempLike.getUserId());
                if (uriUser.equals("")) {
                    holder.followerPhoto.setImageResource(R.drawable.profile_p);
                } else {
                    Picasso.with(adapterContext).load(uriUser).fit().into(holder.followerPhoto);
                }

                holder.followerName.setText(tempLike.getUserId());

                FirebaseEventPost originPost = getOriginPost(tempLike.getEventPostId());

                int numOfPhotos = originPost.getPhotos().size();

                if (numOfPhotos >1){
                    holder.followeeName.setVisibility(View.VISIBLE);
                    holder.followeeName.setText("...");
                    holder.doing.setText("likes your photos");
                }else{
                    holder.doing.setText("likes your photo");
                }
                holder.time.setText(dateFormat.format(tempLike.getTime()));
                Picasso.with(adapterContext).load(originPost.getPhotos().get(0)).fit().into(holder.photoLike);



                holder.photoLike.setVisibility(View.VISIBLE);
                holder.followeePhoto.setVisibility(View.GONE);
                holder.comment.setVisibility(View.GONE);


                break;
            case ACTIVITIES:
                Log.d("ActivityFeedAdapter", "Change to Activities.");


                FirebaseRelationship tempFollowing = followingActivities.get(getItemCount() - 1 - position);
                holder.followerName.setText(tempFollowing.getFollower());
                holder.doing.setText("starts following");
                holder.followeeName.setText(tempFollowing.getFollowee());
                holder.time.setText(dateFormat.format(tempFollowing.getTime()));


                String uriFollowerTemp = getPhotoURI(tempFollowing.getFollower());
                if (uriFollowerTemp.equals("")) {
                    holder.followerPhoto.setImageResource(R.drawable.profile_p);
                } else {
                    Picasso.with(adapterContext).load(uriFollowerTemp).fit().into(holder.followerPhoto);
                }


                String uriFolloweeTemp = getPhotoURI(tempFollowing.getFollowee());

                if (uriFolloweeTemp.equals("")) {
                    holder.followeePhoto.setImageResource(R.drawable.profile_p);
                } else {
                    Picasso.with(adapterContext).load(uriFolloweeTemp).fit().into(holder.followeePhoto);
                }

                holder.photoLike.setVisibility(View.GONE);
                holder.followeeName.setVisibility(View.VISIBLE);
                holder.followeePhoto.setVisibility(View.VISIBLE);
                holder.comment.setVisibility(View.GONE);
                break;

            case COMMENTS:
                holder.followeeName.setVisibility(View.INVISIBLE);
                holder.followeePhoto.setVisibility(View.INVISIBLE);
                holder.comment.setVisibility(View.VISIBLE);
                FirebaseEventComment tempComment = myComments.get(getItemCount() - 1 - position);
                holder.doing.setText("leaves a comment on");


                String uriPhoto = getPhotoURI(tempComment.getUserId());
                if (uriPhoto.equals("")) {
                    holder.followerPhoto.setImageResource(R.drawable.profile_p);
                } else {
                    Picasso.with(adapterContext).load(uriPhoto).fit().into(holder.followerPhoto);
                }

                FirebaseEventPost commentPost = getOriginPost(tempComment.getEventPostId());
                holder.photoLike.setVisibility(View.VISIBLE);
                int numPhotos = commentPost.getPhotos().size();

                if (numPhotos >1){
                    holder.followeeName.setVisibility(View.VISIBLE);
                    holder.followeeName.setText("...");

                }
                Picasso.with(adapterContext).load(commentPost.getPhotos().get(0)).fit().into(holder.photoLike);

                holder.comment.setText(tempComment.getComment());
                holder.followerName.setText(tempComment.getUserId());
                holder.time.setText(dateFormat.format(tempComment.getTime()));


                break;
        }
    }

    @Override
    public int getItemCount() {
        switch (currentMode) {
            case FOLLOW_ME:
                return allFollowMe.size();
            case LIKE_ME:
                return allLikeMe.size();
            case ACTIVITIES:
                return followingActivities.size();
            case COMMENTS:
                return myComments.size();
        }

        return 0;
    }

    public String getPhotoURI(String userID) {
        String uri = "";
        for (FirebaseUser user : allUsers) {
            if (user.getUserID().equals(userID)) {
                if (user.getAvatar() != null) {
                    uri = user.getAvatar().toASCIIString();
                }
                break;
            }
        }
        return uri;
    }

    public FirebaseEventPost getOriginPost(String postId){
        for (FirebaseEventPost fp : allPosts){
            if (fp.getEventPostId().equals(postId)){
                return fp;
            }
        }
        return null;
    }

    // Update Methods
    public void updateData() {
        updateFollowMe();
        updateFollowingActivities();
        updateLikeMe();
        updateMyComment();
        notifyDataSetChanged();
    }

    public void updateFollowMe() {
        ArrayList<FirebaseRelationship> targets = new ArrayList<>();
        for (FirebaseRelationship fr : allRelationships) {
            if (fr.getFollowee().equals(currentUser) && fr.getStatus())
                targets.add(fr);
        }
        allFollowMe = targets;
    }

    public void updateFollowingActivities() {
        ArrayList<String> myFollowing = new ArrayList<>();
        for (FirebaseRelationship fr : allRelationships) {
            if (fr.getFollower().equals(currentUser) && fr.getStatus())
                myFollowing.add(fr.getFollowee());
        }

        ArrayList<FirebaseRelationship> targets = new ArrayList<>();
        for (FirebaseRelationship fr : allRelationships) {
            if (myFollowing.contains(fr.getFollower()) && fr.getStatus())
                targets.add(fr);
        }

        followingActivities = targets;

    }

    public void updateLikeMe() {
        ArrayList<String> myPostsID = new ArrayList<>();
        for (FirebaseEventPost fp : allMyPosts) {
            if (fp.getUserId().equals(currentUser))
                myPostsID.add(fp.getEventPostId());
        }

        Collections.sort(allLikes, new Comparator<FirebaseEventLike>() {
            @Override
            public int compare(FirebaseEventLike like1, FirebaseEventLike like2) {
                if (like1.getTime().before(like2.getTime())) {
                    return -1;
                } else if (like1.getTime().after(like2.getTime())) {
                    return 1;
                }
                return 0;
            }
        });

        ArrayList<FirebaseEventLike> targets = new ArrayList<>();
        for (FirebaseEventLike fl : allLikes) {
            if (myPostsID.contains(fl.getEventPostId())) {
                if (fl.getStatus()) {
                    targets.add(fl);
                } else {
                    targets.remove(fl);
                }
            }
        }
        allLikeMe = targets;

    }

    public void updateMyComment() {
        ArrayList<String> allFollowing = new ArrayList<>();
        for (FirebaseRelationship fp : allRelationships) {
            if (fp.getFollower().equals(currentUser) && fp.getStatus())
                allFollowing.add(fp.getFollowee());
        }

        ArrayList<FirebaseEventComment> targets = new ArrayList<>();
        for (FirebaseEventComment fl : showComments) {
            if (allFollowing.contains(fl.getUserId())) {
                targets.add(fl);
            }
        }
        myComments = targets;

    }

    // Operations for ArrayList allUsers
    public void addUser(FirebaseUser user) {
        this.allUsers.add(user);
        updateData();
    }

    public void updateUser(FirebaseUser user) {
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUserID().equals(user.getUserID())) {
                allUsers.set(i, user);
            }
        }
        updateData();
    }

    public void removeUser(FirebaseUser user) {
        ArrayList<FirebaseUser> targets = new ArrayList<>();
        for (FirebaseUser fu : allUsers) {
            if (!fu.getUserID().equals(user.getUserID()))
                targets.add(fu);
        }
        allUsers = targets;
        updateData();
    }


    // Operations for ArrayList allRelationships
    public void addRelationship(FirebaseRelationship relationship) {
        this.allRelationships.add(relationship);
        updateData();
    }

    public void updateRelationship(FirebaseRelationship relationship) {
        for (int i = 0; i < allRelationships.size(); i++) {
            if ((allRelationships.get(i).getFollowee().equals(relationship.getFollowee())) && (allRelationships.get(i).getFollowee().equals(relationship.getFollowee()))) {
                allRelationships.set(i, relationship);
            }
        }
        updateData();
    }

    public void removeRelationship(FirebaseRelationship relationship) {
        ArrayList<FirebaseRelationship> targets = new ArrayList<>();
        for (FirebaseRelationship fr : allRelationships) {
            if (!(fr.getFollowee().equals(relationship.getFollowee()) && fr.getFollower().equals(relationship.getFollower())))
                targets.add(fr);
        }
        allRelationships = targets;
        updateData();
    }

    // Operations for ArrayList allMyPosts
    public void addComments(FirebaseEventComment comment) {
        this.showComments.add(comment);
        updateData();
    }

    public void updateComments(FirebaseEventComment comment) {
        for (int i = 0; i < showComments.size(); i++) {
            if (showComments.get(i).getEventCommentId().equals(comment.getEventCommentId())) {
                showComments.set(i, comment);
            }
        }
        updateData();
    }

    public void removeComment(FirebaseEventComment comment) {
        ArrayList<FirebaseEventComment> targets = new ArrayList<>();
        for (FirebaseEventComment fu : showComments) {
            if (!fu.getEventCommentId().equals(comment.getEventCommentId()))
                targets.add(fu);
        }
        showComments = targets;
        updateData();
    }

    // Operations for ArrayList allLikes
    public void addLike(FirebaseEventLike like) {
        this.allLikes.add(like);
        updateData();
    }

    public void updateLike(FirebaseEventLike like) {
        for (int i = 0; i < allLikes.size(); i++) {
            if (allLikes.get(i).getEventLikeId().equals(like.getEventLikeId())) {
                allLikes.set(i, like);
            }
        }
        updateData();
    }

    public void removeLike(FirebaseEventLike like) {
        ArrayList<FirebaseEventLike> targets = new ArrayList<>();
        for (FirebaseEventLike fu : allLikes) {
            if (!fu.getEventLikeId().equals(like.getEventLikeId()))
                targets.add(fu);
        }
        allLikes = targets;
        updateData();
    }

    // Operations for ArrayList allMyPosts
    public void addPost(FirebaseEventPost post) {
        this.allMyPosts.add(post);
        updateData();
    }

    public void updatePost(FirebaseEventPost post) {
        for (int i = 0; i < allMyPosts.size(); i++) {
            if (allMyPosts.get(i).getEventPostId().equals(post.getEventPostId())) {
                allMyPosts.set(i, post);
            }
        }
        updateData();
    }

    public void removePost(FirebaseEventPost post) {
        ArrayList<FirebaseEventPost> targets = new ArrayList<>();
        for (FirebaseEventPost fu : allMyPosts) {
            if (!fu.getEventPostId().equals(post.getEventPostId()))
                targets.add(fu);
        }
        allMyPosts = targets;
        updateData();
    }

    // Operations for ArrayList allMyPosts
    public void addAllPost(FirebaseEventPost post) {
        this.allPosts.add(post);
        updateData();
    }

    public void updateAllPost(FirebaseEventPost post) {
        for (int i = 0; i < allPosts.size(); i++) {
            if (allPosts.get(i).getEventPostId().equals(post.getEventPostId())) {
                allPosts.set(i, post);
            }
        }
        updateData();
    }

    public void removeAllPost(FirebaseEventPost post) {
        ArrayList<FirebaseEventPost> targets = new ArrayList<>();
        for (FirebaseEventPost fu : allPosts) {
            if (!fu.getEventPostId().equals(post.getEventPostId()))
                targets.add(fu);
        }
        allPosts = targets;
        updateData();
    }

    // ViewHolder
    public static class ActivityFeedViewHolder extends RecyclerView.ViewHolder {
        private final ImageView followerPhoto;
        private final TextView followerName;
        private final TextView doing;

        private final ImageView followeePhoto;
        private final TextView followeeName;

        private final ImageView photoLike;

        private final TextView time;
        private final TextView comment;


        public ActivityFeedViewHolder(View itemView) {
            super(itemView);
            followerPhoto = itemView.findViewById(R.id.followerPhoto);
            followerName = itemView.findViewById(R.id.followerName);
            doing = itemView.findViewById(R.id.start);

            followeePhoto = itemView.findViewById(R.id.followeePhoto);
            followeeName = itemView.findViewById(R.id.followeeName);

            photoLike = itemView.findViewById(R.id.photo);

            time = itemView.findViewById(R.id.time);
            comment = itemView.findViewById(R.id.comment);

        }
    }

    // Mode Selection
    public void setMode(int mode) {
        this.currentMode = mode;
        Log.d("ActivityFeedAdapter", String.valueOf(currentMode));
        Log.d("AllLikeMe",String.valueOf(allLikeMe.size()));
        updateData();
    }
}
