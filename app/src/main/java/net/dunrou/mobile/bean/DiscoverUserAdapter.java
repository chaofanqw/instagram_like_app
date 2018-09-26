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
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;
import net.dunrou.mobile.fragment.DiscoverFragment;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

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
    private ArrayList<SuggestedUser> suggestedUsers_commonFriends;
    private ArrayList<SuggestedUser> suggestedUsers_search;

    private ArrayList<FirebaseRelationship> allRelationships;
    private ArrayList<SuggestedUser> allUsers;

    public static final int COMMON_FRIENDS = 1;
    public static final int TOP = 2;
    public static final int TEST = 3;
    public static final int SEARCH = 4;

    public int suggestMode = COMMON_FRIENDS;

    public static Comparator<SuggestedUser> valueComparator;

    public DiscoverUserAdapter() {}

    public DiscoverUserAdapter(Context context) {
        mContext = context;

        suggestedUsers_test = new ArrayList<>();
        suggestedUsers_top = new ArrayList<>();
        suggestedUsers_commonFriends = new ArrayList<>();
        suggestedUsers_search = new ArrayList<>();

        allUsers = new ArrayList<>();
        allRelationships = new ArrayList<>();

        valueComparator = new Comparator<SuggestedUser>() {
            @Override
            public int compare(SuggestedUser su1, SuggestedUser su2) {
                return su2.getValue() - su1.getValue();
            }
        };

        SuggestedUser user1 = new SuggestedUser("userID01");
        SuggestedUser user2 = new SuggestedUser("userID2", R.drawable.pm1);
        SuggestedUser user3 = new SuggestedUser("userID3", R.drawable.pokemon157);
        SuggestedUser user4 = new SuggestedUser("userID4", R.drawable.lx3);
        SuggestedUser user5 = new SuggestedUser("userID5", R.drawable.lx1);
        SuggestedUser user6 = new SuggestedUser("userID6", R.drawable.pokemon124);
        SuggestedUser user7 = new SuggestedUser("userID7");
        suggestedUsers_test.add(user1);
        suggestedUsers_test.add(user2);
        suggestedUsers_test.add(user3);
        suggestedUsers_test.add(user4);
        suggestedUsers_test.add(user5);
        suggestedUsers_test.add(user6);
        suggestedUsers_test.add(user7);

        new FirebaseUtil().setRelationshipsListener();
        new FirebaseUtil().setUsersListener();
    }

    @Override
    public DiscoverUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiscoverUserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_search_item, null));
    }

    @Override
    public void onBindViewHolder(DiscoverUserViewHolder holder, int position) {
        final SuggestedUser user;

        switch (suggestMode) {
            case TOP:
                user = suggestedUsers_top.get(position);
                holder.mDescription_TV.setVisibility(View.VISIBLE);
                break;
            case COMMON_FRIENDS:
                user = suggestedUsers_commonFriends.get(position);
                holder.mDescription_TV.setVisibility(View.VISIBLE);
                break;
            case TEST:
                user = suggestedUsers_test.get(position);
                holder.mDescription_TV.setVisibility(View.GONE);
                break;
            case SEARCH:
                user = suggestedUsers_search.get(position);
                holder.mDescription_TV.setVisibility(View.GONE);
                break;
            default:
                user = suggestedUsers_commonFriends.get(position);
                holder.mDescription_TV.setVisibility(View.VISIBLE);
        }

        holder.mUserID_TV.setText(user.getUserID());
        holder.mDescription_TV.setText("  " + user.getValue() + user.getDescription());
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
                Log.d(TAG, "click follow: " + MainActivity.CURRENT_USERID + " -> " + user.getUserID());
                Date date = Calendar.getInstance().getTime();
                FirebaseRelationship firebaseRelationship = new FirebaseRelationship(
                        null, MainActivity.CURRENT_USERID, user.getUserID(), date, true);

//                TODO check existed or not
                firebaseRelationInsertUpdate(firebaseRelationship);
            }
        });

        holder.mUnfollow_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click unfollow: " + MainActivity.CURRENT_USERID + " -> " + user.getUserID());
                Date date = Calendar.getInstance().getTime();
                FirebaseRelationship firebaseRelationship = new FirebaseRelationship(
                        null, MainActivity.CURRENT_USERID, user.getUserID(), date, false);

//                TODO check existed or not
                firebaseRelationInsertUpdate(firebaseRelationship);
            }
        });
    }

    @Override
    public int getItemCount() {
        switch (suggestMode) {
            case TOP:
                return suggestedUsers_top.size();
            case COMMON_FRIENDS:
                return suggestedUsers_commonFriends.size();
            case TEST:
                return suggestedUsers_test.size();
            case SEARCH:
                return suggestedUsers_search.size();
            default:
                return suggestedUsers_commonFriends.size();
        }
    }

    /**
     * add a user in allUses when child listener of firebase called.
     */
    public void addUser(FirebaseUser firebaseUser){
        SuggestedUser suggestedUser = new SuggestedUser(firebaseUser.getUserID());
        this.allUsers.add(suggestedUser);
        Log.d(TAG, "addUser: " + suggestedUser.getUserID());
        updateSuggested();
    }

    /**
     * update a user in allUses when child listener of firebase called.
     */
    public void updateUser(FirebaseUser firebaseUser){
        for(int i = 0; i < allUsers.size(); i++) {
            if(allUsers.get(i).getUserID().equals(firebaseUser.getUserID())) {
                allUsers.get(i).setAvatar(firebaseUser.getAvatar());
                allUsers.get(i).setUserID(firebaseUser.getUserID());
            }
        }
        updateSuggested();
    }

    /**
     * remove a user in allUses when child listener of firebase called.
     */
    public void removeUser(FirebaseUser firebaseUser){
        ArrayList<SuggestedUser> targets = new ArrayList<>();
        for(SuggestedUser fu : allUsers) {
            if(!fu.getUserID().equals(firebaseUser.getUserID()))
                targets.add(fu);
        }
        allUsers = targets;
        updateSuggested();
    }

    /**
     * add a relationship in allRelationships when child listener of firebase called.
     */
    public void addRelationship(FirebaseRelationship firebaseRelationship) {
        allRelationships.add(firebaseRelationship);
        updateSuggested();
    }

    /**
     * update a relationship in allRelationships when child listener of firebase called.
     */
    public void updateRelationship(FirebaseRelationship firebaseRelationship) {
        for(int i = 0; i < allRelationships.size(); i ++) {
            if(allRelationships.get(i).getRelationshipId().equals(firebaseRelationship.getRelationshipId())) {
                allRelationships.set(i, firebaseRelationship);
            }
        }
        updateSuggested();
    }

    /**
     * add a relationship in allRelationships when child listener of firebase called.
     */
    public void removeRelationship(FirebaseRelationship firebaseRelationship) {
        ArrayList<FirebaseRelationship> targets = new ArrayList<>();
        for(FirebaseRelationship fr : allRelationships) {
            if(!fr.getRelationshipId().equals(firebaseRelationship.getRelationshipId())) {
                targets.add(fr);
            }
        }
        allRelationships = targets;
        updateSuggested();
    }

    /**
     * update a user's isFollowed field in allUses when child listener of firebase called.
     */
    public void updateAllUsersRelations(){
        for(SuggestedUser suggestedUser : this.allUsers) {
            suggestedUser.setIsFollowed(false);
            for(FirebaseRelationship firebaseRelationship : this.allRelationships) {
                if(suggestedUser.getUserID().equals(firebaseRelationship.getFollowee())
                        && firebaseRelationship.getFollower().equals(MainActivity.CURRENT_USERID)) {
                    suggestedUser.setIsFollowed(firebaseRelationship.getStatus());
                }
            }
        }
    }

    /**
     * update all suggested user lists
     */
    public void updateSuggested() {
        updateAllUsersRelations();

        updateSuggestedUserTop();
        updateSuggestedUserCommonFriends();
        updateSuggestedUserTest();

        reconstructSuggestedUsers();

        notifyDataSetChanged();
    }

    /**
     * insert or update a relation into firebase
     * @param firebaseRelationship
     */
    public void firebaseRelationInsertUpdate(FirebaseRelationship firebaseRelationship) {
        for(FirebaseRelationship relationship: allRelationships) {
            if(relationship.getFollowee().equals(firebaseRelationship.getFollowee())
                    && relationship.getFollower().equals(firebaseRelationship.getFollower())) {
                firebaseRelationship.setRelationshipId(relationship.getRelationshipId());
                new FirebaseUtil().updateRelationship(firebaseRelationship, true);
                return;
            }
        }
        new FirebaseUtil().updateRelationship(firebaseRelationship, false);
    }

    /**
     * update suggestedUsers_commonFriends
     */
    public void updateSuggestedUserTop() {
        resetSuggestedUserTop();
        HashMap<String, Integer> userMap = new HashMap<String, Integer>();
        for(int i = 0; i < allUsers.size(); i++)
            userMap.put(allUsers.get(i).getUserID(), i);

        for(FirebaseRelationship firebaseRelationship : allRelationships) {
            if(firebaseRelationship.getStatus()) {
                int pre_value = allUsers.get(userMap.get(firebaseRelationship.getFollowee())).getValue();
                allUsers.get(userMap.get(firebaseRelationship.getFollowee())).setValue(pre_value + 1);
            }
        }
        suggestedUsers_top = new ArrayList<>(allUsers);
    }

    /**
     * update suggestedUsers_commonFriends
     */
    public void updateSuggestedUserTest() {
        suggestedUsers_test = allUsers;
    }

    /**
     * update suggestedUsers_commonFriends
     */
    public void updateSuggestedUserCommonFriends() {
        HashMap<String, Boolean> friend = new HashMap<>();
        for(FirebaseRelationship firebaseRelationship : this.allRelationships) {
            if(firebaseRelationship.getFollower().equals(MainActivity.CURRENT_USERID)
                    && firebaseRelationship.getStatus())
                friend.put(firebaseRelationship.getFollowee(), firebaseRelationship.getStatus());
        }
//        resetSuggestedUserCommonFriends();
        suggestedUsers_commonFriends.clear();
        for(FirebaseRelationship firebaseRelationship : this.allRelationships) {
            if(friend.containsKey(firebaseRelationship.getFollower()) && !friend.containsKey(firebaseRelationship.getFollowee()))
                updateSuggestedUserCommonFriend(firebaseRelationship.getFollowee(), firebaseRelationship.getStatus());
        }
    }

    /**
     * update the value of a suggestedUsers_commonFriend in suggestedUsers_commonFriends
     * @param userID
     */
    public void updateSuggestedUserCommonFriend(String userID, Boolean status) {
        if(status && !userID.equals(MainActivity.CURRENT_USERID)) {
            for(SuggestedUser suggestedUser : suggestedUsers_commonFriends) {
                if(suggestedUser.getUserID().equals(userID)) {
                    suggestedUser.setValue(suggestedUser.getValue() + 1);
                    if(suggestedUser.getValue() > 1)
                        suggestedUser.setDescription(" common friends");
                    return;
                }
            }

            for(SuggestedUser suggestedUser : allUsers) {
                if(suggestedUser.getUserID().equals(userID)) {
                    SuggestedUser suggestedUser_cf = new SuggestedUser(suggestedUser);
                    suggestedUser_cf.setValue(1);
                    suggestedUser_cf.setDescription(" common friend");
                    suggestedUsers_commonFriends.add(suggestedUser_cf);
                    return;
                }
            }
        }
    }

    /**
     * set the values of all suggested user in suggestedUsers_commonFriends to zero
     */
    public void resetSuggestedUserCommonFriends() {
        for(int i = 0; i < suggestedUsers_commonFriends.size(); i++) {
            suggestedUsers_commonFriends.get(i).setValue(0);
        }
        for(SuggestedUser suggestedUser : suggestedUsers_commonFriends) {
//            suggestedUser.setValue(0);
            Log.d(TAG, "value after reset: " + suggestedUser.getValue());
        }
    }

    /**
     * set the values of all suggested user in allUsers to zero
     */
    public void resetSuggestedUserTop() {
        for(int i = 0; i < allUsers.size(); i++) {
            allUsers.get(i).setValue(0);
        }
    }

    /**
     * remove item with value == 0
     * sort the list based on the "value" field
     */
    public void reconstructSuggestedUsers() {
        reconstructSuggestedUsersTop();
        reconstructSuggestedUsersCommonFriend();
    }

    /**
     * remove item with value == 0
     * sort the list based on the "value" field
     */
    public void reconstructSuggestedUsersCommonFriend() {
        ArrayList<SuggestedUser> targets1 = new ArrayList<>();
        for(int i = 0; i < suggestedUsers_commonFriends.size(); i++) {
            if(suggestedUsers_commonFriends.get(i).getValue() != 0)
                targets1.add(suggestedUsers_commonFriends.get(i));
        }
        suggestedUsers_commonFriends = targets1;
        Collections.sort(suggestedUsers_commonFriends, valueComparator);
    }

    /**
     * remove item with value == 0
     * remove item with isFollowed = true
     * sort the list based on the "value" field
     */
    public void reconstructSuggestedUsersTop() {
        ArrayList<SuggestedUser> targets2 = new ArrayList<>();
        for(int i = 0; i < suggestedUsers_top.size(); i++) {
            if(suggestedUsers_top.get(i).getValue() != 0 && !suggestedUsers_top.get(i).getIsFollowed())
                targets2.add(suggestedUsers_top.get(i));
        }
        suggestedUsers_top = targets2;
        Collections.sort(suggestedUsers_top, valueComparator);
    }

    public static class DiscoverUserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mAvatar_IV;
        private final TextView mUserID_TV;
        private final Button mFollow_BT;
        private final Button mUnfollow_BT;
        private final TextView mDescription_TV;

        private DiscoverUserViewHolder(View view) {
            super(view);
            mAvatar_IV = (ImageView) view.findViewById(R.id.avatar_IV);
            mUserID_TV = (TextView) view.findViewById(R.id.userID_TV);
            mFollow_BT = (Button) view.findViewById(R.id.follow_BT);
            mUnfollow_BT = (Button) view.findViewById(R.id.unfollow_BT);
            mDescription_TV = (TextView) view.findViewById(R.id.description_TV);
        }
    }

    public void setSuggestMode(int suggestMode) {
        this.suggestMode = suggestMode;
        notifyDataSetChanged();
    }
}
