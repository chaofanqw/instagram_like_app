package net.dunrou.mobile.bean;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.dunrou.mobile.R;
import net.dunrou.mobile.activity.MainActivity;
import net.dunrou.mobile.base.SuggestedUser;
import net.dunrou.mobile.base.firebaseClass.FirebaseRelationship;
import net.dunrou.mobile.fragment.DiscoverFragment;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yvette on 2018/9/19.
 */

public class DiscoverUserAdapter extends RecyclerView.Adapter<DiscoverUserAdapter.DiscoverUserViewHolder> {

    private final static String TAG = DiscoverUserAdapter.class.getSimpleName();

    private DiscoverFragment mDiscoverFragment;
    private Context mContext;
    private View mView;

    private ArrayList<SuggestedUser> suggestedUsers_test;
    private ArrayList<SuggestedUser> suggestedUsers_top;
    private ArrayList<SuggestedUser> suggestedUsers_people;
    private ArrayList<FirebaseRelationship> relationships;

    public DiscoverUserAdapter() {}

    public DiscoverUserAdapter(Context context) {
        mContext = context;
        suggestedUsers_test = new ArrayList<>();
        suggestedUsers_top = new ArrayList<>();
        suggestedUsers_people = new ArrayList<>();

        relationships = new ArrayList<>();

        SuggestedUser user01 = new SuggestedUser("userID01");
        SuggestedUser user02 = new SuggestedUser("userID02");
        SuggestedUser user03 = new SuggestedUser("userID03");
        SuggestedUser user2 = new SuggestedUser("userID2", R.drawable.pm1);
        SuggestedUser user3 = new SuggestedUser("userID3", R.drawable.pokemon157);
        SuggestedUser user4 = new SuggestedUser("userID4", R.drawable.lx3);
        SuggestedUser user5 = new SuggestedUser("userID5", R.drawable.lx1);
        SuggestedUser user6 = new SuggestedUser("userID6", R.drawable.pokemon124);
        SuggestedUser user7 = new SuggestedUser("userID7");
        suggestedUsers_test.add(user01);
        suggestedUsers_test.add(user02);
        suggestedUsers_test.add(user03);
        suggestedUsers_test.add(user2);
        suggestedUsers_test.add(user3);
        suggestedUsers_test.add(user4);
        suggestedUsers_test.add(user5);
        suggestedUsers_test.add(user6);
        suggestedUsers_test.add(user7);

        new FirebaseUtil().getSuggestedUserInformation();
        new FirebaseUtil().getRelationships(MainActivity.currentUserID);
    }

    @Override
    public DiscoverUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiscoverUserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_search_item, null));
    }

    @Override
    public void onBindViewHolder(DiscoverUserViewHolder holder, int position) {
        final SuggestedUser user = suggestedUsers_test.get(position);
        holder.mUserID_TV.setText(user.getUserID());
        if(user.getAvatarID() != -1)
            holder.mAvatar_IV.setImageResource(user.getAvatarID());
        else
            holder.mAvatar_IV.setImageResource(R.drawable.profile_p);

        if(user.getIsFollowed()) {
            holder.mUnfollow_BT.setVisibility(View.VISIBLE);
            holder.mFollow_BT.setVisibility(View.GONE);
        }
        else {
            holder.mFollow_BT.setVisibility(View.VISIBLE);
            holder.mUnfollow_BT.setVisibility(View.GONE);
        }

        holder.mFollow_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click follow: " + MainActivity.currentUserID + " -> " + user.getUserID());
                Date date = Calendar.getInstance().getTime();
                FirebaseRelationship firebaseRelationship = new FirebaseRelationship(
                        null, MainActivity.currentUserID, user.getUserID(), date, true);

//                TODO check existed or not
                firebaseRelationInsertUpdate(firebaseRelationship);
            }
        });

        holder.mUnfollow_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click unfollow: " + MainActivity.currentUserID + " -> " + user.getUserID());
                Date date = Calendar.getInstance().getTime();
                FirebaseRelationship firebaseRelationship = new FirebaseRelationship(
                        null, MainActivity.currentUserID, user.getUserID(), date, false);

//                TODO check existed or not
                firebaseRelationInsertUpdate(firebaseRelationship);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestedUsers_test.size();
    }

    public static class DiscoverUserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mAvatar_IV;
        private final TextView mUserID_TV;
        private final Button mFollow_BT;
        private final Button mUnfollow_BT;

        private DiscoverUserViewHolder(View view) {
            super(view);
            mAvatar_IV = (ImageView) view.findViewById(R.id.avatar_IV);
            mUserID_TV = (TextView) view.findViewById(R.id.userID_TV);
            mFollow_BT = (Button) view.findViewById(R.id.follow_BT);
            mUnfollow_BT = (Button) view.findViewById(R.id.unfollow_BT);
        }
    }

    public void addUser(SuggestedUser suggestedUser){
        for(SuggestedUser user : suggestedUsers_test){
            if(user.getUserID().equals(suggestedUser.getUserID()))
                return;
        }
        suggestedUsers_test.add(suggestedUser);
        this.notifyDataSetChanged();
    }

    public static class NewUserEvent {

        private final SuggestedUser suggestedUser;

        public NewUserEvent(SuggestedUser suggestedUser) {
            this.suggestedUser = suggestedUser;
        }

        public SuggestedUser getSuggestedUser() {
            return suggestedUser;
        }
    }

    public static class RelationAddedEvent {
        private final FirebaseRelationship firebaseRelationship;

        public RelationAddedEvent(FirebaseRelationship firebaseRelationship) {
            this.firebaseRelationship = firebaseRelationship;
        }

        public FirebaseRelationship getFirebaseRelationship() {
            return firebaseRelationship;
        }
    }

    public static class RelationAddFailEvent {}

    public static class UpdateRelationshipEvent {

        private ArrayList<FirebaseRelationship> firebaseRelationships;

        public UpdateRelationshipEvent(ArrayList<FirebaseRelationship> firebaseRelationships) {
            this.firebaseRelationships = firebaseRelationships;
        }

        public ArrayList<FirebaseRelationship> getFirebaseRelationships() {
            return firebaseRelationships;
        }
    }

    public void setRelationship(ArrayList<FirebaseRelationship> firebaseRelationships) {
        this.relationships = firebaseRelationships;

        for(FirebaseRelationship firebaseRelationship : this.relationships) {
            updateRelation(firebaseRelationship.getFollowee(), firebaseRelationship.getStatus());
        }
        this.notifyDataSetChanged();
    }

    public void updateRelation(String userID, Boolean isRelation) {
        for (SuggestedUser suggestedUser : suggestedUsers_test) {
            if (suggestedUser.getUserID().equals(userID))
                suggestedUser.setFollowed(isRelation);
//            Log.d(TAG, "updateRelation: " + suggestedUser.getUserID() + " " + suggestedUser.getIsFollowed());
        }
    }

    public void firebaseRelationInsertUpdate(FirebaseRelationship firebaseRelationship) {
        for(FirebaseRelationship relationship: relationships) {
            if(relationship.getFollowee().equals(firebaseRelationship.getFollowee())) {
                firebaseRelationship.setRelationshipId(relationship.getRelationshipId());
                new FirebaseUtil().relationUpdate(firebaseRelationship, true);
                return;
            }
        }
        new FirebaseUtil().relationUpdate(firebaseRelationship, false);
    }


}
