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

import java.util.ArrayList;

import butterknife.OnClick;

/**
 * Created by yvette on 2018/9/19.
 */

public class DiscoverUserAdapter extends RecyclerView.Adapter<DiscoverUserAdapter.DiscoverUserViewHolder> {

    private final static String TAG = DiscoverUserAdapter.class.getSimpleName();

    private Context mContext;
    private View mView;

    private ArrayList<UserDioscover> mockUsers = new ArrayList<UserDioscover>() {{
        UserDioscover user1 = new UserDioscover("userID1",
                -1);
        UserDioscover user2 = new UserDioscover("userID2",
                 R.drawable.pm1);
        UserDioscover user3 = new UserDioscover("userID3",
                R.drawable.pokemon157);
        UserDioscover user4 = new UserDioscover("userID4",
                R.drawable.lx3);
        UserDioscover user5 = new UserDioscover("userID5",
                R.drawable.lx1);
        UserDioscover user6 = new UserDioscover("userID6",
                R.drawable.pokemon124);
        add(user1);
        add(user2);
        add(user3);
        add(user4);
        add(user5);
        add(user6);
    }};

    public DiscoverUserAdapter() {}

    public DiscoverUserAdapter(Context context) {
        mContext = context;
    }

    @Override
    public DiscoverUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiscoverUserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_search_item, null));
    }

    @Override
    public void onBindViewHolder(DiscoverUserViewHolder holder, int position) {
        UserDioscover user = mockUsers.get(position);
        holder.mUserID_TV.setText(user.getUserID());
        if(user.getAvatarID() != -1)
            holder.mAvatar_IV.setImageResource(user.getAvatarID());
    }

    @Override
    public int getItemCount() {
        return mockUsers.size();
    }

    public class DiscoverUserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mAvatar_IV;
        private final TextView mUserID_TV;
//        private final Button mFollow_BT;

        private DiscoverUserViewHolder(View view) {
            super(view);
            mView = view;
            mAvatar_IV = (ImageView) mView.findViewById(R.id.avatar_IV);
            mUserID_TV = (TextView) mView.findViewById(R.id.userID_TV);
//            mFollow_BT = (Button) mView.findViewById(R.id.follow_BT);
        }
    }

    @OnClick(R.id.follow_BT)
    public void setFollow() {
        Log.d(TAG, "follow button click");
    }

    public class UserDioscover{
        private String userID;
//        private URI avatar;
        private int avatarID = -1;

//        public UserDioscover(String userID, Uri avatar, Button follow_BT) {
//            this.userID = userID;
////            this.avatar = avatar;
//            this.follow_BT = follow_BT;
//        }

        public UserDioscover(String userID, int avatarID) {
            this.userID = userID;
            this.avatarID = avatarID;
        }

        public String getUserID() {
            return userID;
        }

        public int getAvatarID() {
            return avatarID;
        }

        //        public Uri getAvatar() {
//            return avatar;
//        }
    }
}
