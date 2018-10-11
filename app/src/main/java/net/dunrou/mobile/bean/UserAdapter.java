package net.dunrou.mobile.bean;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import net.dunrou.mobile.R;
import net.dunrou.mobile.activity.ProfileActivity;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventLike;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserAdapter extends RecyclerView.Adapter {

    private ArrayList<FirebaseUser> allUsers;
    private ArrayList<FirebaseEventLike> allLikes;

    private Context context;
    private ArrayList<FirebaseEventLike> likeList;
    private String eventPostId;

    public UserAdapter() {
    }

    public UserAdapter(Context context, String eventPostId) {
        this.context = context;
        likeList = new ArrayList<>();
        allUsers = new ArrayList<>();
        allLikes = new ArrayList<>();
        this.eventPostId = eventPostId;
        new FirebaseUtil().getAllUsersData();
        new FirebaseUtil().getLikeData();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_user_item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rootHolder, int position) {
        ViewHolder holder = (ViewHolder) rootHolder;
        FirebaseEventLike likeItem = likeList.get(position);
        holder.username.setText(likeItem.getUserId());
        String time = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(likeItem.getTime());
        String description = "Liked this post at " + time;
        holder.createTime.setText(description);
        String uriUser = getPhotoURI(likeItem.getUserId());
        String defaultAvatar = "android.resource://net.dunrou.mobile/" + R.drawable.profile_p;
        setImage(context, holder.avatar, uriUser.equals("") ? defaultAvatar : uriUser);
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    private void setImage(Context context, ImageView imageView, String url) {
        Picasso.with(context).load(url)
                .placeholder(R.drawable.ic_default_color)
                .error(R.drawable.ic_default_color)
                .into(imageView);
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

    public void updateData() {
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
        for (FirebaseEventLike like : allLikes) {
            if (eventPostId.equals(like.getEventPostId())) {
                if (like.getStatus()) {
                    targets.add(like);
                } else {
                    targets.remove(like);
                }
            }
        }
        likeList = targets;

        Collections.sort(likeList, new Comparator<FirebaseEventLike>() {
            @Override
            public int compare(FirebaseEventLike like1, FirebaseEventLike like2) {
                if (like1.getTime().before(like2.getTime())) {
                    return 1;
                } else if (like1.getTime().after(like2.getTime())) {
                    return -1;
                }
                return 0;
            }
        });

        notifyDataSetChanged();
    }

    public void addUser(FirebaseUser user) {
        this.allUsers.add(user);
        updateData();
    }

    public void addLike(FirebaseEventLike like) {
        this.allLikes.add(like);
        updateData();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.liked_username) TextView username;
        @BindView(R.id.liked_time) TextView createTime;
        @BindView(R.id.liked_user_avatar) CircleImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.liked_user_avatar)
        public void avatarListener() {
            displayProfile();
        }

        @OnClick(R.id.liked_username)
        public void usernameListener() {
            displayProfile();
        }

        public void displayProfile() {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("CURRENT_USERID", username.getText().toString());
            context.startActivity(intent);
        }
    }
}
