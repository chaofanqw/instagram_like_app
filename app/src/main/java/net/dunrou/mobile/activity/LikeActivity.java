package net.dunrou.mobile.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.dunrou.mobile.R;
import net.dunrou.mobile.base.message.ActivityFeedMessage;
import net.dunrou.mobile.bean.UserAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LikeActivity extends AppCompatActivity {

    private String eventPostId;
    private UserAdapter adapter;
    private RecyclerView likedGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        eventPostId = getIntent().getStringExtra("postID");
        likedGrid = findViewById(R.id.like_list);
        adapter = new UserAdapter(this, eventPostId);
        likedGrid.setAdapter(adapter);
        likedGrid.setLayoutManager(new LinearLayoutManager(this));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAddedEvent(ActivityFeedMessage.UserAddedEvent userAddedEvent) {
        adapter.addUser(userAddedEvent.getUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPosts(ActivityFeedMessage.LikeAddedEvent newLike) {
        adapter.addLike(newLike.getLike());
    }
}
