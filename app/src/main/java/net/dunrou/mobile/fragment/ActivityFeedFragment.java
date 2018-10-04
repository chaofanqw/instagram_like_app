package net.dunrou.mobile.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.dunrou.mobile.R;
import net.dunrou.mobile.base.message.ActivityFeedMessage;
import net.dunrou.mobile.base.message.ProfileMessage;
import net.dunrou.mobile.bean.ActivityFeedAdapter;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by wei on 2018/9/29.
 */
public class ActivityFeedFragment extends Fragment {

    private transient final static String TAG = DiscoverFragment.class.getSimpleName();

    private View activityFeedView;
    private Context activityFeedContext;

    private RecyclerView recyclerView;
    private ActivityFeedAdapter afAdapter;

    private TabLayout tabLayout;
    private TabLayout.Tab follow;
    private TabLayout.Tab like;
    private TabLayout.Tab activities;

    public ActivityFeedFragment() {
        // Required empty public constructor
    }

    public static ActivityFeedFragment newInstance() {
        ActivityFeedFragment fragment = new ActivityFeedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFeedContext = getActivity().getApplicationContext();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activityFeedView = inflater.inflate(R.layout.fragment_activitiy_feed, container, false);

        initializeActivityFeed();
        initializeAdapter();

        return activityFeedView;
    }

    public void initializeActivityFeed() {
        recyclerView = activityFeedView.findViewById(R.id.rV);

        tabLayout = activityFeedView.findViewById(R.id.tL);
        follow = tabLayout.getTabAt(0);
        like = tabLayout.getTabAt(1);
        activities = tabLayout.getTabAt(2);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        afAdapter.setMode(afAdapter.FOLLOW_ME);
                        break;
                    case 1:
                        afAdapter.setMode(afAdapter.LIKE_ME);
                        break;
                    case 2:
                        afAdapter.setMode(afAdapter.ACTIVITIES);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


    }

    public void initializeAdapter() {
        afAdapter = new ActivityFeedAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(afAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAddedEvent(ActivityFeedMessage.UserAddedEvent userAddedEvent) {
        afAdapter.addUser(userAddedEvent.getUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserChangedEvent(ActivityFeedMessage.UserChangedEvent userChangedEvent) {
        afAdapter.updateUser(userChangedEvent.getUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserRemovedEvent(ActivityFeedMessage.UserRemovedEvent userRemovedEvent) {
        afAdapter.removeUser(userRemovedEvent.getUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationshipAddedEvent(ActivityFeedMessage.RelationshipAddedEvent relationshipChildAddedEvent) {
        afAdapter.addRelationship(relationshipChildAddedEvent.getFirebaseRelationship());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationshipChangedEvent(ActivityFeedMessage.RelationshipChangedEvent relationshipChildAddedEvent) {
        afAdapter.updateRelationship(relationshipChildAddedEvent.getFirebaseRelationship());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationshipRemovedEvent(ActivityFeedMessage.RelationshipRemovedEvent relationshipChildAddedEvent) {
        afAdapter.removeRelationship(relationshipChildAddedEvent.getFirebaseRelationship());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPosts(ActivityFeedMessage.PostAddedEvent newPost) {
        afAdapter.addPost(newPost.getPost());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePosts(ActivityFeedMessage.PostChangedEvent newPost) {
        afAdapter.updatePost(newPost.getPost());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemovePosts(ActivityFeedMessage.PostRemovedEvent newPost) {
        afAdapter.removePost(newPost.getPost());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPosts(ActivityFeedMessage.LikeAddedEvent newLike) {
        afAdapter.addLike(newLike.getLike());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePosts(ActivityFeedMessage.LikeChangedEvent newLike) {
        afAdapter.updateLike(newLike.getLike());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemovePosts(ActivityFeedMessage.LikeRemovedEvent newLike) {
        afAdapter.removeLike(newLike.getLike());
    }

}
